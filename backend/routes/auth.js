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
        jwt.sign(payload, 'kiskecske', { expiresIn: 3600 }, (error, token) => {
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
           return response.status(401).json({ msg: 'Invalid credentials' });
        }
        const isMatch = await user.comparePassword(password);
        if (!isMatch) {
            return response.status(401).json({ msg: 'Invalid credentials' });
        }
        const payload = { user: { id: user.id } };
        jwt.sign(payload, 'kiskecske', { expiresIn: 36000 }, (error, token) => {
            if (error) throw error;
            response.cookie('token', token, {
                httpOnly: true,
                sameSite: 'Strict'
            });
            return response.status(200).json({ msg: 'Login successful' });
        });
    } catch (error) {
        console.error(error.message);
        return response.status(500).send('Internal Server Error');
    }
});

router.get('/verify-token', (request, response) => {
    try {
        const token = request.cookies.token;
        
        if (!token) {
            console.log('Token is not found');
            return response.status(401).json({ isAuthenticated: false}); 
        }
    
        jwt.verify(token, 'kiskecske', (error, user) => {
            if (error) {
                console.log('Token is invalid');
                return response.status(401).json({ isAuthenticated: false });
            }
    
            return response.status(200).json({ isAuthenticated: true });
        });
    } catch (error) {
        console.error(error.message);
        return response.status(500).send('Internal Server Error');
    }
});

module.exports = router;
