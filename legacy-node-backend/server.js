require('dotenv').config();
const express = require('express');
const cookieParser = require('cookie-parser');
const mongodb = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');
const path = require('path')
const authRoutes = require('./routes/auth');
const userRoutes = require('./routes/user');
const dotenv = require('dotenv').config();

console.log(dotenv.parsed);

const application = express();

application.use(cors());
application.use(cookieParser());
application.use(bodyParser.json());

application.use('/api/auth', authRoutes);
application.use('/api/user', userRoutes);
application.use(express.static(path.join(__dirname, '../frontend/dist/frontend/browser')));

application.get('*', (request, response) => {
    response.sendFile(path.join(__dirname, '../frontend/dist/frontend/browser', 'index.html'));
});

mongodb.connect(`${dotenv.parsed.MONGODB_URI}`, { useNewUrlParser: true, useUnifiedTopology: true})
    .then(() => console.log('MongoDB Connected'))
    .catch(err => console.log(err));

application.listen(dotenv.parsed.APP_PORT, () => {
    console.log(`Application running on port on http://localhost:${dotenv.parsed.APP_PORT}`);
});
