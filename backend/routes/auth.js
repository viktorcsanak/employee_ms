const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User = require('../models/User');

router.post('/register', async (request, response) => {
    const {
        email,
        password,
        confirmPassword,
        firstName,
        middleName,
        lastName,
        dateOfBirth,
        placeOfResidence: {
            city,
            address,
            postalCode
        },
        position,
        startOfEmployment,
        gender,
        adminPrivilege,
        hrManagementAccess,
    } = request.body;

    try {
        let user = await User.findOne({ email });
        if (user) {
            return response.status(400).json({ msg: 'User already exists!'});
        }

        user = new User(request.body);
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
            console.log('email %s invalid', email);
           return response.status(401).json({ msg: 'Invalid credentials' });
        }
        const isMatch = await user.comparePassword(password);
        if (!isMatch) {
            console.log('password invalid');
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

router.post('/logout', async (request, response) => {
    try {
        const token = request.cookies.token;
        
        if (!token) {
            console.log('Token is not found');
            return response.status(404).json({ msg: 'No token was found'}); 
        }
    
        response.cookie('token', token, {
            httpOnly: true,
            sameSite: 'Strict',
            expires: new Date(0),
        });

        return response.status(200).json({ msg: 'Logout successful' });
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

router.get('/verify-admin', async (request, response) => {
    try {
        const token = request.cookies.token;
        
        if (!token) {
            console.log('Token is not found');
            return response.status(401).json({ isAuthenticated: false});
        }
    
        const decoded = jwt.verify(token, 'kiskecske');
        const user = await User.findById(decoded.user.id);

        if (!user.adminPrivileges) {
            console.log('User is not admin');
            return response.status(401).json({ isAuthenticated: false});
        }
        return response.status(200).json({ isAuthenticated: true});
    } catch (error) {
        if (error.name === 'JsonWebTokenError') {
            return response.status(403).json({ message: "Token validation failed" });
        }
        console.error(error.message);
        return response.status(500).send('Internal Server Error');
    }
});

router.get('/verify-hr', async (request, response) => {
    try {
        const token = request.cookies.token;
        
        if (!token) {
            console.log('Token is not found');
            return response.status(401).json({ isAuthenticated: false});
        }
    
        const decoded = jwt.verify(token, 'kiskecske');
        const user = await User.findById(decoded.user.id);

        if (!user.hrManagementAccess) {
            return response.status(401).json({ isAuthenticated: false});
        }
        return response.status(200).json({ isAuthenticated: true});
    } catch (error) {
        console.error(error.message);
        if (error.name === 'JsonWebTokenError') {
            return response.status(403).json({ message: "Token validation failed" });
        }
        return response.status(500).send('Internal Server Error');
    }
});

module.exports = router;
