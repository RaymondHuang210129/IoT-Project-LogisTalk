
class fsm():
    def __init__(self):
        self.state = 'init'
        self.address = ''
        self.cart = {}
        self.pendingItem = None

    def get_state(self):
        return self.state

    def arbitrary_msg(self):
        return {'result': True}

    def push_set_address(self):
        if self.state == 'init':
            self.state = 'wait_for_address'
            return {'result': True}
        else: 
            return {'result': False, 'content': 'Not in init state'}

    def set_address(self, address):
        if self.state == 'wait_for_address':
            self.address = address
            self.state = 'init'
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in set_address state'}

    def push_new_order(self):
        if self.state == 'init':
            #print(type(self.address), type(self.state))
            if self.address != '':
                self.state = 'establishment'
                return {'result': True}
            else:
                return {'result': False, 'content': 'Address not set'}
        else:
            return {'result': False, 'content': 'Not in init state'}

    def push_cancel_order(self):
        if self.state == 'establishment':
            self.cart = {}
            self.state = 'init'
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in establishment state'}

    def push_show_cart(self):
        if self.state == 'establishment':
            return {'result': True, 'content': self.cart}
        else:
            return {'result': False, 'content': 'Not in establishment state'}

    def push_select_item(self):
        if self.state == 'establishment':
            self.state = 'item_list'
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in establishment state'}

    def push_cancel_select_item(self):
        if self.state == 'item_list':
            self.state = 'establishment'
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in item_list state'}



    def push_put_into_cart(self):
        #do nothing
        if self.state == 'count':
#           self.state = 'count'
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in item_list state'}

    def postback_put_into_cart(self, item):
        if self.state == 'item_list':
            self.state = 'count'
            self.pendingItem = item
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in count state'}


    def push_cancel_count(self):
        if self.state == 'count':
            self.pendingItem = None,
            self.state = 'item_list'
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in count state'}

    def push_count(self, count):
        if self.state == 'count':
            self.cart.update({self.pendingItem: int(count)})
            self.pendingItem = None
            self.state = 'establishment' 
            return {'result': True}
        else:
            return {'result': False, 'content': 'Not in count state'}

    def push_send_order(self):
        if self.state == 'establishment':
            if bool(self.cart):
                self.state = 'init'
                cart = self.cart
                self.cart = {}
                return {'result': True, 'content': {'order': cart, 'address': self.address}}
            else:
                return {'result': False, 'content': 'No item in cart'}
        else:
            return {'result': False, 'content': 'Not in establishment state'}
