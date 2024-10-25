const Session = require('../models/LoginSessions')

async function checkSession(request, response, next) {
    try {
        const token = request.cookies.token;

        if (!token) {
            console.error('Token was not found');
            return response.status(401).json('Token was not found');
        }

        const session = await Session.findOne({ token });

        if (!session) {
            response.cookie('token', token, {
                httpOnly: true,
                sameSite: 'Strict',
                expires: new Date(0),
            });

            return response.status(403).json('Your session was closed');
        }

        next();
    } catch (error) {
        return response.status(500).json({message: 'session internal error'});
    }
}

module.exports = checkSession;
