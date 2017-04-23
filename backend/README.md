# Cloud Firebase Functions Backend
Cloud Functions for Firebase lets you run backend code to events triggered by Firebase features and `HTTPS` requests. Once the functions are deployed, Google's servers manage the functions without you having to worry about scalability. To learn more about Cloud Functions, check [this link](https://firebase.google.com/docs/functions/).

## Getting started
```
$ sudo apt-get install nodejs
$ cd functions
$ npm install
$ cd ..
```
You can change the backend logic and various `HTTP` functions in `functions/index.js`. To deploy the changes to the live server, enter the following command,
```
$ firebase deploy --only functions
```
**Note:** As of today, there is no straight-forward way to test your changes on a local Firebase server. Please deploy the changes with extra caution and test one function at a time.
