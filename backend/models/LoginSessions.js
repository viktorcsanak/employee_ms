const mongodb = require('mongoose');
const bcrypt = require('bcryptjs');
const { triggerAsyncId } = require('async_hooks');

const LoginSessionsSchema = new mongodb.Schema({
    id: {
        type: String,
        required: true,
    },
    token: {
        type: String,
        required: true,
        unique: true
    },
    //TODO: operating system, browser, IP address(hashed) to detect if a device is already logged in
});

//TODO: hash IP before storing it
/* LoginSessionsSchema.pre('save', async function (next) {
    if(!this.isModified('ipAddress')) {
        return next();
    }

    const salt = await bcrypt.genSalt(10);
    this.password = await bcrypt.hash(this.password, salt);
    next();
});

LoginSessionsSchema.pre('updateOne', async function (next) {
    const update = this.getUpdate();

    if (update.$set.password) {
        const salt = await bcrypt.genSalt(10);
        update.$set.password = await bcrypt.hash(update.$set.password, salt);
    }

    next();
});

LoginSessionsSchema.methods.compareIPAddress = async function (ipAddress) {
}; */

module.exports = mongodb.model('LoginSessions', LoginSessionsSchema);
