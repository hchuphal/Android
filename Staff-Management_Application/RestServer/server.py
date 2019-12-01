# -*- coding: utf-8 -*-

from flask import Flask
from flask_restful import Resource, Api, reqparse

app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()

class Team07_Server(Resource):
    def get(self):
        return {     
			    "destination": "Gate",
			    "firstName": "Himanshu",
			    "gender": "M",
			    "lastName": "Chuphal",
			    "staffId": 1,
			    "status": "Available",
			    "task": "Ticket Counter",
			    "taskScheduleDate": "04/04/2019",
			    "timeToDestination": "10"
  			}
            
    def post(self):
        parser.add_argument('quote', type=str)
        args = parser.parse_args()

        return {
            'status': True,
            'timeToDestination': '{} added. Good'.format(args['quote'])
        }

    def put(self, id):
        parser.add_argument('quote', type=str)
        args = parser.parse_args()

        return {
            'id': id,
            'status': True,
            'timeToDestination': '{} was updated.'.format(id)
        }

api.add_resource(Team07_Server, '/')

if __name__ == '__main__':
    app.run(debug=True)
