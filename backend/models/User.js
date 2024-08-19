const mongodb = require('mongoose');
const bcrypt = require('bcryptjs');
const { triggerAsyncId } = require('async_hooks');

const UserSchema = new mongodb.Schema({
    email: {
        type: String,
        required: true,
        unique: true
    },
    password: {
        type: String,
        required: true
    },
    firstName: {
        type: String,
        required: true
    },
    middleName: {
        type: String
    },
    lastName: {
        type: String,
        required: true
    },
    dateOfBirth: {
        type: Date,
        required: true
    },
    cellularPhone: {
        type: String,
    },
    placeOfResidence: {
        city: {
            type: String,
            required: true
        },
        postalCode: {
            type: String,
            required: true
        }, 
        address: {
            type: String,
            required: true
        },

    },
    position: {
        type: String,
        required: true
    },
    startOfEmployment: {
        type: Date,
        required: true
    },
    gender: {
        type: String,
        required: true
    },
    adminPrivileges: {
        type: Boolean,
        required: true
    },
    hrManagementAccess: {
        type: Boolean,
        required: true
    }
});

UserSchema.pre('save', async function (next) {
    if(!this.isModified('password')) {
        return next();
    }

    const salt = await bcrypt.genSalt(10);
    this.password = await bcrypt.hash(this.password, salt);
    next();
});

UserSchema.methods.comparePassword = async function (password) {
    return bcrypt.compare(password, this.password);
};

module.exports = mongodb.model('User', UserSchema);
