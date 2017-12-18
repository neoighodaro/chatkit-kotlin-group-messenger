
const express = require('express');
const bodyParser = require('body-parser');
const Chatkit = require('pusher-chatkit-server');

const app = express();
const chatkit = new Chatkit.default({
    key: "d6ce2e1a-37fb-47fb-915d-ba552acc44aa:SIi72zGNBiV+q0bNo7eXWPJVDWMJjlGmsJATm7/yB6Q=",
    instanceLocator: "v1:us1:a198a551-439e-4b78-af06-477e7bbd110d",
});

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.get('/users', (req, res) => {
    const username = req.query.username;

    chatkit.createUser(username, username)
         .then(r => res.json({username}))
         .catch(e => res.json({error: e.error_description, type: e.error_type}));
});

app.post('/auth', (req, res) => {
    const userId = req.query.user_id;

    res.json(chatkit.authenticate({grant_type: "client_credentials"}, userId));
});

app.get('/', (req, res, next) => {
    res.json("Working!");
})

app.listen(3000, () => console.log('Running application...'));
