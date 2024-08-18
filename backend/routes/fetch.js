const express = require('express');
const router = express.Router();
const jwt = require('jsonwebtoken');
const User = require('../models/User');

router.get('/user-data', async (request, response) => {
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

module.exports = router;
