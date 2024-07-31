const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User = require('../models/User');

router.post('/register', async (request, response) => {
    const {email, password} = request.body;

    try {
        let user = await User.findOne( { email });
        if (user) {
            return response.status(400).json({ msg: 'User already exists!'});
        }
        
        user = new User({ email, password });
        await user.save();
        const payload = {user: { id: user.id }};
        jwt.sign(payload, 'kiskecse', { expiresIn: 3600 }, (error, token) => {
            if (error) throw error;
            response.json({ token });
        });
    } catch (error) {
        console.error(error.message);
        return response.status(500).send('Internal Server Error!');
    }
});

router.post('/login', async (request, response) => {
    const { email, password } = request.body;
    try {
        let user = await User.findOne({ email });
        if (!user) {
           return res.status(400).json({ msg: 'Invalid credentials' });
        }
        const isMatch = await user.comparePassword(password);
        if (!isMatch) {
            return res.status(400).json({ msg: 'Invalid credentials' });
        }
        const payload = { user: { id: user.id } };
        jwt.sign(payload, process.env.JWT_SECRET, { expiresIn: 36000 }, (error, token) => {
            if (error) throw error;
            res.json({ token });
        });
    } catch (error) {
        console.error(error.message);
        res.status(500).send('Internal Server Error');
    }
});

module.exports = router;
