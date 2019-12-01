//const functions = require('firebase-functions');

// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
//
var functions = require('firebase-functions')
var express = require('express')
// for typescript use :
// import * as functions from 'firebase-functions'
// import * as express from 'express'

const app = express();
// listen the path suffix /:task
app.get('/:task', (req, res) => {
    switch(req.params.task.toLowerCase()){ // test the :task value
        case "janitor":
             res.send([{
                id: 1, 
                destination: "Roller_Coaster",
                firstName: "Oscar",
                gender: "M",
                lastName: "Siby",
                staffId: 1,
                status: "Available",
                task: "Janitor",
                taskScheduleDate: "07/04/2019",
                timeToDestination: "20"
            }])
            break;
        case "testuser":
            res.send({
                id: 420, 
                name: "Himanshu", 
                task: "test",
                timeToDestination: "10",
                status: "Available"
            })
            break;
         case "security":
             res.send([{
                id: 2, 
                destination: "Gate",
                firstName: "Johan",
                gender: "M",
                lastName: "Wallin",
                staffId: 2,
                status: "Available",
                task: "security",
                taskScheduleDate: "07/04/2019",
                timeToDestination: "13"
            }])
            break;
        case "ticketsales":
             res.send([{
                id: 3, 
                destination: "Counter",
                firstName: "Per",
                gender: "M",
                lastName: "Magnus",
                staffId: 3,
                status: "Available",
                task: "TicketSales",
                taskScheduleDate: "07/04/2019",
                timeToDestination: "17"
            }])
            break;
         case "restaurantattendant":
             res.send([{
                id: 4, 
                destination: "Restaurant",
                firstName: "Lina",
                gender: "M",
                lastName: "Theorn",
                staffId: 4,
                status: "Available",
                task: "RestaurantAttendant",
                taskScheduleDate: "07/04/2019",
                timeToDestination: "12"
            }])
            break;
        case "janitortime":
             res.send([{
                destination: "Roller_Coaster",
                task: "Janitor"
            }])
            break;
         case "restaurantattendanttime":
             res.send([{
                destination: "Restaurantat",
                task: "RestaurantAttendant"
            }])
            break;
         case "ticketsalestime":
             res.send([{
                destination: "Tikcet Counter",
                task: "TicketSales"
            }])
            break;
         case "securitytime":
             res.send([{
                destination: "Gate",
                task: "Security"
            }])
            break;
        default: // send an error otherwise
            res.status(404).send({message: "Staff not found"})
    }
});

// listen the path prefix /staff
exports.staff = functions.https.onRequest(app);
