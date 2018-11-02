const express = require('express');
const bodyParser = require('body-parser');
const Chatkit = require('@pusher/chatkit-server');

const app = express();
const chatkit = new Chatkit.default({
    key: "CHATKIT_KEY",
    instanceLocator: "INSTANCE_LOCATOR",
});

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: false }));

app.get('/users', (req, res) => {
  const username = req.query.username;

  chatkit.createUser({ id: username, name: username })
    .then(r => res.json({username}))
    .catch(e => res.json({error: e.error_description, type: e.error_type}));
});

app.post('/auth', (req, res) => {
  const userId = req.query.user_id;

  res.json(chatkit.authenticate({ userId: userId }));
});

app.get('/', (req, res, next) => {
  res.json("Working!");
})

app.listen(3000, () => console.log('Running application...'));
