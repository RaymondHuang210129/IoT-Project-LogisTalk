# -*- coding: utf-8 -*-

#Python module requirement: line-bot-sdk, flask
from flask import Flask, request, abort
from linebot import LineBotApi, WebhookHandler
from linebot.exceptions import InvalidSignatureError 
from linebot.models import (MessageEvent, PostbackEvent, 
							TextMessage, TextSendMessage, 
							TemplateSendMessage, ButtonsTemplate, 
							URITemplateAction, MessageTemplateAction, PostbackTemplateAction
							)

import requests
import time
import json
from bs4 import BeautifulSoup
import time, DAN, requests, random

import threading
import time

import json

import uuid

from db import *
from fsm import *
from LineBotResponse import *
from Profile import *

from geopy.geocoders import GoogleV3


ServerURL = Profile.IotTalk.serverurl
Reg_addr = Profile.IotTalk.regaddr
DAN.profile['dm_name']='FP_LineBot3'
DAN.profile['df_list']=['FP_LB3_msg_pull', 'FP_LB3_trace_pull']
DAN.profile['d_name']= None # None for autoNaming
DAN.device_registration_with_retry(ServerURL, Reg_addr)


line_bot_api = LineBotApi(Profile.linebot.apikey) #LineBot's Channel access token
handler = WebhookHandler(Profile.linebot.channelsecret)        #LineBot's Channel secret

#data structures that will be replaced by DB:
lineId_fsm_bind = dict()
device_location_bind = dict()



static_orderId = 0;             

app = Flask(__name__)

def orderIdGen():
	global static_orderId
	static_orderId += 1
	return static_orderId

def loadUserId():
	try:
		idFile = open('idfile', 'r')
		idList = idFile.readlines()
		idFile.close()
		idList = idList[0].split(';')
		idList.pop()
		return idList
	except Exception as e:
		print(e)
		return None


def saveUserId(userId):
		idFile = open('idfile', 'a')
		idFile.write(userId+';')
		idFile.close()


@app.route("/", methods=['GET'])
def hello():
	return "HTTPS Test OK."

@app.route("/", methods=['POST'])
def callback():
	signature = request.headers['X-Line-Signature']    # get X-Line-Signature header value
	body = request.get_data(as_text=True)              # get request body as text
	print("Request body: " + body, "Signature: " + signature)
	try:
		handler.handle(body, signature)                # handle webhook body
	except InvalidSignatureError:
		abort(400)
	return 'OK'


@handler.add(MessageEvent, message=TextMessage)
def handle_message(event):
	global lineId_fsm_bind
	Msg = event.message.text
	lineBotResponse = LineBotResponse()
	if Msg == 'Hello, world': return

	#Add new lineId in Users table
	Users.get_or_create(lineID = event.source.user_id)

	#Add new fsm for new user
	if not event.source.user_id in lineId_fsm_bind.keys():
		lineId_fsm_bind.update({event.source.user_id: fsm()})

	if Msg == '追蹤訂單':
		if lineId_fsm_bind[event.source.user_id].get_state() == 'init':
			response = []
			query = (Orders.select(Orders)
						   .join(Users, on = (Orders.userID == Users.userID))
				           .where(Users.lineID == event.source.user_id)
					 )
			for order in query:
				sub_res = {'orderId': order.orderUID, 'status': order.status, 'items': {}}
				sub_query = (OrdersItemsXRef.select(OrdersItemsXRef, Items)
											.join(Items, on = (OrdersItemsXRef.itemID == Items.itemID))
											.where(OrdersItemsXRef.orderID == order)
							 )
				for xref in sub_query:
					sub_res['items'].update({xref.itemID.itemName: xref.itemNumber})
				response.append(sub_res)
			line_bot_api.reply_message(event.reply_token, 
									   [LineBotResponse().trace_order(response), 
										LineBotResponse().welcome_message()
										])
		else:
			input_arbitrary_msg(event)
	elif Msg == '設定運送地址':
		if lineId_fsm_bind[event.source.user_id].push_set_address()['result']:
			line_bot_api.reply_message(event.reply_token, LineBotResponse().address_request())
		else:
			input_arbitrary_msg(event)
	elif len(Msg) > 3 and Msg[:3] == '地址是':
		if lineId_fsm_bind[event.source.user_id].set_address(Msg[3:])['result']:
			line_bot_api.reply_message(event.reply_token, 
									   [LineBotResponse().address_confirm(Msg[3:]), 
										LineBotResponse().welcome_message()
									    ])
		else:
			input_arbitrary_msg(event)
	elif Msg == '建立新訂單':
		result = lineId_fsm_bind[event.source.user_id].push_new_order()
		if result['result']:
			line_bot_api.reply_message(event.reply_token, LineBotResponse().establishment())
		else: 
			if result['content'] == 'Address not set':
				line_bot_api.reply_message(event.reply_token, 
										   [LineBotResponse().no_address(), 
											LineBotResponse().welcome_message()
											])
			else:
				input_arbitrary_msg(event)
	elif Msg == '取消訂單':
		if lineId_fsm_bind[event.source.user_id].push_cancel_order()['result']:
			line_bot_api.reply_message(event.reply_token, LineBotResponse().welcome_message())
		else:
			input_arbitrary_msg(event)
	elif Msg == '查看購物車':
		result = lineId_fsm_bind[event.source.user_id].push_show_cart()
		if result['result']:
			if bool(result['content']):
				line_bot_api.reply_message(event.reply_token, 
										   [LineBotResponse().view_cart(result['content']), 
											LineBotResponse().establishment()
											])
			else:
				line_bot_api.reply_message(event.reply_token, 
										   [LineBotResponse().no_item_in_cart(), 
											LineBotResponse().establishment()
											])
		else:
			input_arbitrary_msg(event)
	elif Msg == '選擇商品':
		if lineId_fsm_bind[event.source.user_id].push_select_item()['result']:
			query = Items.select()
			response = []
			for item in query:
				#print(item.itemImage)
				response.append({'id': item.itemID, 
								 'name': item.itemName, 
								 'price': item.itemPrice, 
								 'image': item.itemImage
								 })
			line_bot_api.reply_message(event.reply_token, LineBotResponse().item_list(response))
		else:
			input_arbitrary_msg(event)
	elif Msg == '返回我的訂單':
		if lineId_fsm_bind[event.source.user_id].push_cancel_select_item()['result']:
			line_bot_api.reply_message(event.reply_token, LineBotResponse().establishment())
		else:
			input_arbitrary_msg(event)
	elif Msg == '放入購物車':
		if lineId_fsm_bind[event.source.user_id].push_put_into_cart()['result']:
			pass
			#do nothing, postback handler will deal with it
#			line_bot_api.reply_message(event.reply_token, LineBotResponse().count_request())
		else:
			input_arbitrary_msg(event)
	elif Msg == '返回商品列表':
		if lineId_fsm_bind[event.source.user_id].push_cancel_count()['result']:
			query = Items.select()
			response = []
			for item in query:
				response.append({'id': item.itemID, 
								 'name': item.itemName, 
								 'price': item.itemPrice, 
								 'image': item.itemImage
								 })
			line_bot_api.reply_message(event.reply_token, LineBotResponse().item_list(response))
		else:
			input_arbitrary_msg(event)
	elif Msg[0].isdigit() and len(Msg) >= 2 and Msg[1] == '個':
		if lineId_fsm_bind[event.source.user_id].push_count(Msg[0])['result']:
			line_bot_api.reply_message(event.reply_token, [
					LineBotResponse().item_added(), 
					LineBotResponse().establishment()
				])
		else:
			input_arbitrary_msg(event)
	elif Msg == '送出訂單':
		result = lineId_fsm_bind[event.source.user_id].push_send_order()
		if result['result'] == True:
			orderuid = str(uuid.uuid1())
			userid = Users.select().where(Users.lineID == event.source.user_id)[0]
			addr = result['content']['address']
			geocoding = GoogleV3(api_key = Profile.googleV3.apikey).geocode(query = addr)
			print(geocoding.latitude, geocoding.longitude)
			orderid = Orders.insert(orderUID = orderuid, 
									userID = userid, 
									driverID = None, 
									address = addr, 
									status = 0, 
									addressLat = geocoding.latitude, 
									addressLong = geocoding.longitude,
									).execute()
			for itemName, count in result['content']['order'].items():
				itemid = (Items.select().where(Items.itemName == itemName))[0]
				OrdersItemsXRef.insert(orderID = orderid, 
									   itemID = itemid, 
									   itemNumber = count
									   ).execute()
			line_bot_api.reply_message(event.reply_token, 
									   [LineBotResponse().order_sent({'orderId': orderuid, 
																	  'items': result['content']['order']
																	  }), 
										LineBotResponse().welcome_message()
										])
		else:
			if result['content'] == 'No item in cart':
				line_bot_api.reply_message(event.reply_token,
										   [LineBotResponse().no_item_in_cart(), 
											LineBotResponse().establishment()
											])
			else:
				input_arbitrary_msg(event)
	else:
		if lineId_fsm_bind[event.source.user_id].get_state() == 'wait_for_address':
			lineId_fsm_bind[event.source.user_id].set_address(Msg)
			line_bot_api.reply_message(event.reply_token, 
									   [LineBotResponse().address_confirm(Msg), 
										LineBotResponse().welcome_message()
										])
		else:
			current_state = lineId_fsm_bind[event.source.user_id].get_state()
			if current_state == 'init':
				line_bot_api.reply_message(event.reply_token, LineBotResponse().welcome_message())
			elif current_state == 'establishment':
				line_bot_api.reply_message(event.reply_token, LineBotResponse().establishment())
			elif current_state == 'item_list':
				query = Items.select()
				response = []
				for item in query:
					response.append({'id': item.itemID, 
									 'name': item.itemName, 
									 'price': item.itemPrice, 
									 'image': item.itemImage
									 })
				line_bot_api.reply_message(event.reply_token, LineBotResponse().item_list(response))
			elif current_state == 'count':
				print("else")
				line_bot_api.reply_message(event.reply_token, LineBotResponse().count_request())
		
def input_arbitrary_msg(event):
	global lineId_fsm_bind
	current_state = lineId_fsm_bind[event.source.user_id].get_state()
	if current_state == 'init':
		line_bot_api.reply_message(event.reply_token, LineBotResponse().welcome_message())
	elif current_state == 'establishment':
		line_bot_api.reply_message(event.reply_token, LineBotResponse().establishment())
	elif current_state == 'item_list':
		query = Items.select()
		response = []
		for item in query:
			response.append({'id': item.itemID, 
							 'name': item.itemName, 
							 'price': item.itemPrice, 
							 'image': item.itemImage
							 })
		line_bot_api.reply_message(event.reply_token, LineBotResponse().item_list(response))
	elif current_state == 'count':
		line_bot_api.reply_message(event.reply_token, LineBotResponse().count_request())



	
@handler.add(PostbackEvent)
def handle_postback(event):
	global lineId_fsm_bind
	query = Items.select().where(Items.itemID == int(event.postback.data))
	if lineId_fsm_bind[event.source.user_id].postback_put_into_cart(query[0].itemName)['result']:
		line_bot_api.reply_message(event.reply_token, LineBotResponse().count_request())
		#print('pendingItem: {}'.format(query[0].itemName))
	else:
		pass



def trace():
	global device_location_bind
	while True:
		rawInput = DAN.pull('FP_LB3_trace_pull')
		if rawInput != None:
			content = json.loads(str(rawInput[0]))
			device_location_bind.update({content['driverId']: [content['lat'], content['long']]})
			print(content['driverId'], 'move to', content['lat'], content['long'])


def server():
	while True:
		rawInput = DAN.pull('FP_LB3_msg_pull')
		if rawInput != None and bool(rawInput[0]):
			content = json.loads(str(rawInput[0]))
			if content['type'] == 'state_update':
				print("推播通知")
				for orderId in content['content']['orderID']:
					query = (Users.select(Users, Orders)
								  .join(Orders, on = (Users.userID == Orders.userID))
								  .where(Orders.orderID == orderId)
							 )
					lineId = query[0].lineID
					orderUid = query[0].orders_sent_by_user[0].orderUID
					print(orderUid)
					if content['content']['status'] == 1:
						line_bot_api.push_message(lineId, TextSendMessage(text='訂單編號{} 已開始出貨，欲知詳情請輸入「我的訂單狀態」。'.format(orderUid)))
					elif content['content']['status'] == -1:
						line_bot_api.push_message(lineId, TextSendMessage(text='訂單編號{} 已經送達，敬請前往取貨，謝謝您。'.format(orderUid)))
					else:
						print('push message status error')
			else:
				print('status not state_update')





if __name__ == "__main__":
	locationListener = threading.Thread(target=trace)
	locationListener.daemon = True
	locationListener.start()

	webRequestListener = threading.Thread(target=server)
	webRequestListener.daemon = True
	webRequestListener.start()
	
	app.run('127.0.0.1', port=32768, threaded=True, use_reloader=False)





