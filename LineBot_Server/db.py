from peewee import *
import datetime
from Profile import *

db = MySQLDatabase('iot', 
				   user = Profile.db.user, 
				   password = Profile.db.password, 
				   host = Profile.db.host, 
				   port = Profile.db.port, 
				   charset = 'utf8')

class BaseModel(Model):
	class Meta:
		database = db

class Users(BaseModel):
	userID = AutoField()
	lineID = CharField(unique = True)
	class Meta:
		table_name = 'Users'

class Drivers(BaseModel):
	driverID = AutoField()
	driverName = CharField()
	driverImage = CharField()
	account = CharField(unique = True)
	password = CharField()
	driverToken = CharField(null = True, default = None)
	class Meta:
		table_name = 'Drivers'

class Orders(BaseModel):
	orderID = AutoField()
	orderUID = CharField(unique = True)
	userID = ForeignKeyField(Users, backref = 'orders_sent_by_user', db_column = 'userID')
	driverID = ForeignKeyField(Drivers, backref = 'orders_transmited_by_driver', default = None, null = True, db_column = 'driverID')
	address = CharField(max_length = 255)
	addressLat = DoubleField()
	addressLong = DoubleField()
	establishTime = DateTimeField(default = datetime.datetime.now, null = True)
	departTime = DateTimeField(default = None, null = True)
	arriveTIme = DateTimeField(default = None, null = True)
	status = IntegerField()
	class Meta:
		table_name = 'Orders'

class Items(BaseModel):
	itemID = AutoField()
	itemName = CharField(unique = True)
	itemPrice = IntegerField()
	itemImage = CharField()
	class Meta:
		table_name = 'Items'

class OrdersItemsXRef(BaseModel):
	order_item_ID = AutoField()
	orderID = ForeignKeyField(Orders, backref = 'items_in_order', db_column = 'orderID')
	itemID = ForeignKeyField(Items, backref = 'item_detail', db_column = 'itemID')
	itemNumber = IntegerField()
	class Meta:
		table_name = 'OrdersItemsXRef'

if __name__ == '__main__':
	db.connect()
	#db.create_tables([Users, Orders, Drivers, Items, OrdersItemsXRef])
	#db.drop_tables([Users, Orders, Drivers, Items, OrdersItemsXRef])
	#OrdersItemsXRef.delete().where(OrdersItemsXRef.order_item_ID > 0).execute()
	#Orders.delete().where(Orders.orderID > 0).execute()
	

	#Items.get_or_create(itemName = '姆咪餅乾', itemImage = 'https://i.imgur.com/2KPrcAn.jpg', itemPrice = 100)
	#Items.get_or_create(itemName = '姆咪飲料', itemImage = 'https://i.imgur.com/lSdMxPC.jpg', itemPrice = 250)
	#Items.get_or_create(itemName = '姆咪蛋糕', itemImage = 'https://truth.bahamut.com.tw/s01/201706/d50743ebbcef996d5694a264c65e7a62.JPG', itemPrice = 600)