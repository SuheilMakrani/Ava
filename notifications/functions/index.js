'use-strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');

admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.firestore.document("Users/{user_id}/Notifications/{notification_id}").onWrite((change, context)=>{

  const user_id = context.params.user_id;
  const notification_id = context.params.notification_id;

  // console.log("FROM: "+user_id+" TO: "+notification_id);

   return admin.firestore().collection("Users").doc(user_id).collection("Notifications").doc(notification_id).get().then(queryResult => {
     const from_user_id = queryResult.data().sendeeID;
     const from_message = queryResult.data().message;
     const prop = queryResult.data().property_id;

     const from_data = admin.firestore().collection("Users").doc(from_user_id).get();
     const to_data = admin.firestore().collection("Users").doc(user_id).get();

     return Promise.all([from_data,to_data]).then(result=>{

          const from_name = result[0].data().fname;
          const to_name = result[1].data().fname;

          const from_name_lname = result[0].data().lname;
          const to_name_lname = result[1].data().lname;

          const token_id = result[1].data().token_id;

        const payload = {
          notification:{
            title : "",
            body : from_name+" "+from_name_lname+" "+from_message,
            icon : "default",
            click_action: ""
          },data: {
            from_user_id: from_user_id,
            prop: prop,
            from_message: from_message
          }
        };
        return admin.messaging().sendToDevice(token_id, payload).then(result=>{
          console.log("Notification Sent.");
        });
      });
    });
});
