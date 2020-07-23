const express = require('express')
const bodyParser = require('body-parser')
const ngrok = require('ngrok')
const decodeJWT = require('did-jwt').decodeJWT
const { Credentials } = require('uport-credentials')
const transports = require('uport-transports').transport
const message = require('uport-transports').message.util
const fs = require("fs");
const FormData = require("form-data");
const fetch = require("node-fetch");
const nunjucks = require('nunjucks');
const expressNunjucks = require('express-nunjucks');

let endpoint = ''
const app = express();

const isDev = app.get('env') === 'development';
app.set('views',__dirname+ "/views");

const njk = expressNunjucks(app, {
    watch: isDev,
    noCache: isDev
})

app.use(bodyParser.json())

app.use(bodyParser.urlencoded({extended:false}));

var requestStatus= [];

const credentials = new Credentials({
    appName: 'Accessphere Blockchain Attester',
    did: 'did:ethr:0x3e74c1c7f496459b3fab697595696a5bca0f6f72',
    privateKey: '8e47ae8a641bcce91516445f0501c37e79b606dcf758f048b252b68c0074f98c'
  })
  
const profile = {
  name: Accessphere Blockchain Attester',
  profileImage: {
    "/": '/ipfs/QmSs5k4mQS5PtV6mxGmkcu8GYNKgpCK9PAiNVTqjqUkixc'
  }
};
var jwtProfile= "";
const profileJWT = credentials.createVerification({
    sub: 'did:ethr:0x3e74c1c7f496459b3fab697595696a5bca0f6f72',
    claim: profile
  })
  .then((res)=>{
      jwtProfile=res;
  })

app.get('/formIdentidad',(req,res)=> {
    const requestId= Math.floor(Math.random()*1000000);
    requestStatus[requestId]="0";
    res.render('formIdentidad.html',{requestId:requestId});
})

app.get('/status/:requestId',(req,res)=>{
  res.send(requestStatus[requestId]);
})

app.get('/claveUnica', (req, res) => {
  const transportChasqui = transports.qr.chasquiSend()  
  credentials.createDisclosureRequest({
      requested: [],
      notifications: true,
      vc: [ jwtProfile ],
      callbackUrl: transports.messageServer.genCallback()
    }).then(requestToken => {
      console.log(decodeJWT(requestToken))  //log request token to console
      const qr = transportChasqui(requestToken)
      res.render('disclosureRequest.html',{qr:qr,nombre:nombre,requestId:requestId});
    }) 
  })


  app.post('/callback/:requestId', (req, res) => {
    const jwt = req.body.access_token;
    const requestId= req.params.requestId;
    console.log(requestStatus[requestId]);
    credentials.authenticateDisclosureResponse(jwt).then(creds => {
      // take this time to perform custom authorization steps... then,
      // set up a push transport with the provided 
      // push token and public encryption key (boxPub)  
      const push = transports.push.send(creds.pushToken, creds.boxPub)
      const object= requestStatus[requestId];
      console.log(object);
      object['did']=creds.did;
      requestStatus[requestId]=object;
      credentials.createVerification({
        sub: creds.did,
        vc: [ jwtProfile ],
        exp: Math.floor(new Date().getTime() / 1000) + 30 * 24 * 60 * 60,
        claim: {'Cedula Identidad':
                    {
                    'Nombre' : requestStatus[requestId].nombre,
                    'RUT': requestStatus[requestId].rut
                    }
                }
        // Note, the above is a complex (nested) claim. 
        // Also supported are simple claims:  claim: {'Key' : 'Value'}
      }).then(attestation => {
        return push(attestation)  // *push* the notification to the user's uPort mobile app.
      }).then(res => {
        //ngrok.disconnect()
      })
    })
  })
  
  app.get('/formCorreo/:requestId',(req,res)=> {
    res.render('formCorreo.html',{requestId: req.params.requestId});
})

app.post('/submitCorreo', (req, res) => {
    const requestId= req.body.requestId;
    const correo= req.body.correoInput;
    const object= requestStatus[requestId];
    object['correo']=correo;
    requestStatus[requestId]=object;
    credentials.createDisclosureRequest({
      requested: [],
      notifications: true,
      vc: [ jwtProfile ],
      callbackUrl: endpoint + '/callbackCorreo/'+requestId
    }).then(requestToken => {
      console.log(decodeJWT(requestToken))  //log request token to console
      const uri = message.paramsToQueryString(message.messageToURI(requestToken), {callback_type: 'post'})
      const qr =  transports.ui.getImageDataURI(uri)
      res.render('disclosureRequestCorreo.html',{qr:qr,requestId:requestId});
    }) 
  })

  app.post('/callbackCorreo/:requestId', (req, res) => {
    const jwt = req.body.access_token;
    const requestId= req.params.requestId;
    credentials.authenticateDisclosureResponse(jwt).then(creds => {
      // take this time to perform custom authorization steps... then,
      // set up a push transport with the provided 
      // push token and public encryption key (boxPub)  
      const push = transports.push.send(creds.pushToken, creds.boxPub)
      const object= requestStatus[requestId];
      console.log(object);
      object['did']=creds.did;
      requestStatus[requestId]=object;
      credentials.createVerification({
        sub: creds.did,
        vc: [ jwtProfile ],
        exp: Math.floor(new Date().getTime() / 1000) + 30 * 24 * 60 * 60,
        claim: {'Correo Electronico':requestStatus[requestId].correo}
        // Note, the above is a complex (nested) claim. 
        // Also supported are simple claims:  claim: {'Key' : 'Value'}
      }).then(attestation => {
        return push(attestation)  // *push* the notification to the user's uPort mobile app.
      }).then(res => {
        //ngrok.disconnect()
      })
    })
  })

  // run the app server and tunneling service
  const server = app.listen(8088, () => {
    ngrok.connect(8088).then(ngrokUrl => {
      endpoint = ngrokUrl
      console.log(`Login Service running, open at ${endpoint}`)
    })
  })