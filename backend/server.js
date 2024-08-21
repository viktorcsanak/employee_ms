require('dotenv').config();
const express = require('express');
const cookieParser = require('cookie-parser');
const mongodb = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path')
const authRoutes = require('./routes/auth');
const userRoutes = require('./routes/user');

const application = express();
const port = 3000;

application.use(cors());
application.use(cookieParser());
application.use(bodyParser.json());

application.use('/api/auth', authRoutes);
application.use('/api/user', userRoutes);
application.use(express.static(path.join(__dirname, '../frontend/dist/frontend/browser')));

application.get('*', (request, response) => {
    response.sendFile(path.join(__dirname, '../frontend/dist/frontend/browser', 'index.html'));
});

mongodb.connect('mongodb://localhost:27017/meanstack', { useNewUrlParser: true, useUnifiedTopology: true})
    .then(() => console.log('MongoDB Connected'))
    .catch(err => console.log(err));

application.listen(port, () => {
    console.log(`Application running on port on http://localhost:${port}`);
});
