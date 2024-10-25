const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User = require('../models/User');
const Session = require('../models/LoginSessions')
const checkSession = require('../middlewares/checkSession')
const dotenv = require('dotenv').config();

router.post('/register', checkSession, async (request, response) => {
    const token = request.cookies.token;

    try {
        const decoded = jwt.verify(token, `${dotenv.parsed.JWT_SECRET}`);
        const user = await User.findById(decoded.user.id);

        if (!user.adminPrivileges) {
            console.log('User is not admin');
            return response.status(401).json({ isAuthenticated: false, msg: 'Token is invalid'});
        }
    } catch (error) {
        if (error.name === 'JsonWebTokenError') {
            return response.status(403).json({ message: "Token validation failed" });
        } else if (error.name === 'TokenExpiredError') {
            return response.status(401).json({ isAuthenticated: false, msg: 'Token is expired'});
        }
        console.log(error.name);
        return response.status(500).send('Internal Server Error');
    }

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
            return response.status(401).json({ msg: 'Invalid credentials' });
        }

        const payload = { user: { id: user.id } };

        const token = jwt.sign(payload, `${dotenv.parsed.JWT_SECRET}`, { expiresIn: 3600 });

        response.cookie('token', token, {
            httpOnly: true,
            sameSite: 'Strict'
        });
        //TODO: Compare client IP and device info to check whether the user is already logged in on a device.
        //TODO: When a request is received by the session middleware(TODO), check if the requesting and stored IPs match, to prevent tinkering by evesdroppers, in case they got a valid token
        //TODO: Delete session entries when user is logged out or removed.
        //TODO: Add midleware for looking for an active session when calling an API. If session with JWT is not found, instruct the browser to remove the cookie(expires: Date(0))
        //TODO: Use angular router to navigate the browser to the login page in that case.
        let session = new Session({
            id: user.id,
            token: token,
        });
        await session.save();

        return response.status(200).json({ msg: 'Login successful' });
            
    } catch (error) {
        console.error(error.message);
        return response.status(500).send('Internal Server Error');
    }
});

router.post('/logout', checkSession, async (request, response) => {
    try {
        const token = request.cookies.token;
    
        response.cookie('token', token, {
            httpOnly: true,
            sameSite: 'Strict',
            expires: new Date(0),
        });

        const session = await Session.deleteOne({ token }).exec();

        return response.status(200).json({ msg: 'Logout successful' });
    } catch (error) {
        console.error(error.message);
        return response.status(500).send('Internal Server Error');
    }
});

router.get('/verify-token', checkSession, (request, response) => {
    try {
        const token = request.cookies.token;

        jwt.verify(token, `${dotenv.parsed.JWT_SECRET}`, (error, user) => {
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

router.get('/verify-admin', checkSession, async (request, response) => {
    try {
        const token = request.cookies.token;
    
        const decoded = jwt.verify(token, `${dotenv.parsed.JWT_SECRET}`);
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

router.get('/verify-hr', checkSession, async (request, response) => {
    try {
        const token = request.cookies.token;
    
        const decoded = jwt.verify(token, `${dotenv.parsed.JWT_SECRET}`);
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
