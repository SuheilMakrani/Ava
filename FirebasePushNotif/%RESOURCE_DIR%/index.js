'use-strict'

const functions = require('firebase-functions');
const admin = require ('firebase-admin');

exports.sendNotifications = functions.firestore.document("Users/{user_id}/Notiications/{notification_id}").onWrite(event=>{
  const user_id = event.params.user_id;
  const.notification_id = event.params.notification_id;

  console log("User ID: "+user_id+"|| Notifications ID: "+notification_id);
});
