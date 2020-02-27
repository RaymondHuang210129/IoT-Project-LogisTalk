from linebot.models import (MessageEvent, 
							TextMessage, TextSendMessage, 
							TemplateSendMessage, ButtonsTemplate, CarouselTemplate,
							URITemplateAction, MessageTemplateAction, PostbackTemplateAction,
							CarouselColumn
							)

from Profile import *

class LineBotResponse():

	def __init__(self):
		pass
	
	def welcome_message(self):
		return TemplateSendMessage(
			alt_text='welcome message',
			template=ButtonsTemplate(
				thumbnail_image_url='https://blog.tiandiren.tw/wp-content/uploads/2016/07/9-5.jpg',
				title='歡迎使用IotTalk行動購物',
				text='請選擇下列功能：',
				actions=[
					MessageTemplateAction(
						label='建立新訂單',
						text='建立新訂單'
						),
					MessageTemplateAction(
						label='設定運送地址',
						text='設定運送地址'
						),
					MessageTemplateAction(
						label='追蹤訂單',
						text='追蹤訂單'
						)
					]
				)
			)

	def address_request(self):
		return TextSendMessage(text = '請輸入您的送貨地址')

	def address_confirm(self, address):
		return TextSendMessage(text = '好的，已記錄您的送貨地址為{}'.format(address))

	def no_address(self):
		return TextSendMessage(text = '尚未設定您的送貨地址')

	def trace_order(self, info): #info = [{'orderId': 123, 'items': {'item1': 1}, 'status': '未出貨'}]
		if bool(info):
			return TextSendMessage(text = '您的訂單狀態：\n{}'.format(
				' \n'.join(
					['訂單編號{}，購買項目：{}，狀態：{}，詳情請點選以下連結：{}/customer/{}'.format(
						order['orderId'], 
						' '.join(['{}{}個'.format(k, v) for k, v in order['items'].items()]), 
						(lambda: '未發貨' if order['status'] == 0 else ('配送中' if order['status'] == 1 else '已送達'))(),
						Profile.webserver.serverurl,
						order['orderId']
						) for order in info]
					)
				))
		else: return TextSendMessage(text = '你沒有訂單')

	def establishment(self):
		return TemplateSendMessage(
			alt_text='order homepage',
			template=ButtonsTemplate(
				thumbnail_image_url='https://cdn2.ettoday.net/images/1971/d1971370.jpg',
				title='IotTalk行動購物--新訂單',
				text='請選擇下列功能：',
				actions=[
					MessageTemplateAction(
						label='選擇商品',
						text='選擇商品'
						),
					MessageTemplateAction(
						label='查看購物車',
						text='查看購物車'
						),
					MessageTemplateAction(
						label='送出訂單',
						text='送出訂單'
						),
					MessageTemplateAction(
						label='取消訂單',
						text='取消訂單'
						)
					]
				)
			)

	def view_cart(self, info): #info = {'item1': 2, 'item2': 1}
		return TextSendMessage(text = '購物車內容：\n{}'.format(
			'\n'.join(['{} {}個'.format(k, v) for k, v in info.items()])
			))

	def order_sent(self, info): #info = {'orderId': orderId, 'items': {'item1': 2, 'item2': 3}}
		return TextSendMessage(text = '訂單已成立，訂單編號{}，\n購買商品：\n{}'.format(
			info['orderId'], '\n'.join(['{} {}個'.format(k, v) for k, v in info['items'].items()])
			))

	def no_item_in_cart(self):
		return TextSendMessage(text = '購物車內沒有商品')

	def item_list(self, info): #info = [{'id': 2, 'name': '***', 'price': 50 'image': '***'}]
		return TemplateSendMessage(
			alt_text = 'item_list',
			template = CarouselTemplate(
				columns = [
					CarouselColumn(
						thumbnail_image_url = item['image'],
						title = item['name'],
						text = '{}元/個'.format(item['price']),
						actions = [
							PostbackTemplateAction(
								label = '放入購物車',
								text = '放入購物車',
								data = '{}'.format(item['id'])
								),
							MessageTemplateAction(
								label = '返回我的訂單',
								text = '返回我的訂單'
								)]
						) for item in info]
				)
			)

	def count_request(self):
		return TemplateSendMessage(
			alt_text='order homepage',
			template=ButtonsTemplate(
				title='放入購物車',
				text='請選擇數量：(超過3個請以訊息發送)',
				actions=[
					MessageTemplateAction(
						label='1個',
						text='1個'
						),
					MessageTemplateAction(
						label='2個',
						text='2個'
						),
					MessageTemplateAction(
						label='3個',
						text='3個'
						),
					MessageTemplateAction(
						label='返回商品列表',
						text='返回商品列表'
						)
					]
				)
			)

	def item_added(self):
		return TextSendMessage(text = '已放入購物車')











