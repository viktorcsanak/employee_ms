const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User = require('../models/User');

router.get('/', async (request, response) => {
    const token = request.cookies.token;

    try {
        const decoded = jwt.verify(token, 'kiskecske');

        const user = await User.findById(decoded.user.id).exec();
        if (!user) {
            return response.status(404).json("Could not find data for requested user");
        }

        return response.status(200).json({
            email: user.email,
            firstName: user.firstName,
            middleName: user.middleName,
            lastName: user.lastName,
            dateOfBirth: user.dateOfBirth,
            gender: user.gender,
            placeOfResidence: {
                city: user.placeOfResidence.city,
                postalCode: user.placeOfResidence.postalCode,
                address: user.placeOfResidence.address,
            },
            position: user.position,
            startOfEmployment: user.startOfEmployment,
            adminPrivileges: user.adminPrivileges,
            hrManagementAccess: user.hrManagementAccess,
        });
    } catch (error) {
        // Handle errors, including invalid token or user not found
        console.error('Error:', error.message);
        if (error.name === 'JsonWebTokenError') {
            return response.status(403).json({ message: "Token validation failed" });
        }
        return response.status(500).json({ message: "Internal Server Error" });
    }
});

router.get('/admin/all', async (request, response) => {
    const token = request.cookies.token;

    try {
        const decoded = jwt.verify(token, 'kiskecske');

        const user = await User.findById(decoded.user.id).exec();
        if (!user) {
            return response.status(404).json("Could not find data for admin");
        }
        if (!user.adminPrivileges) {
            return response.status(403).json("Requesting user is not an admin!");
        }

        const users = await User.find().exec();

        let data_for_admin = [];

        for (let user of users) {
            data_for_admin.push({
                __id: user.id,
                email: user.email,
                firstName: user.firstName,
                middleName: user.middleName,
                lastName: user.lastName,
                adminPrivileges: user.adminPrivileges,
                hrManagementAccess: user.hrManagementAccess
            })
        }

        return response.status(200).json(data_for_admin);
    } catch (error) {
        // Handle errors, including invalid token or user not found
        console.error('Error:', error.message);
        if (error.name === 'JsonWebTokenError') {
            return response.status(403).json({ message: "Token validation failed" });
        }
        return response.status(500).json({ message: "Internal Server Error" });
    }
});

router.delete('/admin/:id', async (request, response) => {
    const token = request.cookies.token;

    try {
        const decoded = jwt.verify(token, 'kiskecske');

        const user = await User.findById(decoded.user.id).exec();
        if (!user) {
            console.error('Could not find data for admin');
            return response.status(404).json({ msg: 'Could not find data for admin'} );
        }
        if (!user.adminPrivileges) {
            console.error('Requesting user is not an admin!')
            return response.status(403).json({ msg: 'Requesting user is not an admin!' });
        }

        const users = await User.findByIdAndDelete(request.params.id).exec();

        return response.status(200).json({ msg: 'User was removed' });
    } catch (error) {
        // Handle errors, including invalid token or user not found
        console.error('Error:', error.message);
        if (error.name === 'JsonWebTokenError') {
            console.error('Token validation failed');
            return response.status(403).json({ msg: 'Token validation failed' });
        }
        console.error('Internal server error');
        return response.status(500).json({ msg: 'Internal Server Error  ' });
    }
});

module.exports = router;
