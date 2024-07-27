const express = require('express');
const mongodb = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path')

const application = express();
const port = 3000;

application.use(bodyParser.json());
application.use(cors());
application.use(express.static('dist/frontend'));

mongodb.connect('mongodb://localhost:27017/meanstack', { useNewUrlParser: true, useUnifiedTopology: true})
    .then(() => console.log('MongoDB Connected'))
    .catch(err => console.log(err));

application.get('*', (request, response) => {
    response.sendFile(path.resolve(__dirname, 'dist', 'frontend', 'index.html'));
});

application.listen(port, () => {
    console.log(`Application running on port on http://localhost:${port}`);
});
