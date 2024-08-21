const mongodb = require('mongoose');
const User = require('./models/User')
const bcrypt = require('bcryptjs');

async function seedUsers() {
    try {
        await mongodb.connect('mongodb://localhost:27017/meanstack', { useNewUrlParser: true, useUnifiedTopology: true});

        const salt = await bcrypt.genSalt(10);

        const users = [
            {
                email: 'viktor@company.io',
                password: await bcrypt.hash('wildcard', salt),
                firstName: 'Viktor',
                middleName: '',
                lastName: 'Csanak',
                dateOfBirth: new Date(2000, 6, 1),
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'not-disclosed',
                    postalCode: '24000'
                },
                position: 'Medior Embedded Software Engineer',
                startOfEmployment: new Date(2022, 7,1),
                gender: 'Male',
                adminPrivileges: true,
                hrManagementAccess: true,
            },
            {
                email: 'jane.doe@company.io',
                password: await bcrypt.hash('hrmanagement', salt),
                firstName: 'Jane',
                middleName: '',
                lastName: 'Doe',
                dateOfBirth: new Date(1989, 3, 12),
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'not-disclosed',
                    postalCode: '24000'
                },
                position: 'Lead HR Manager',
                startOfEmployment: new Date(2015, 8, 30),
                gender: 'Female',
                adminPrivileges: false,
                hrManagementAccess: true,
            },
            {
                email: 'bob@company.io',
                password: await bcrypt.hash('admin123', salt),
                firstName: 'Bob',
                middleName: '',
                lastName: 'Ace',
                dateOfBirth: new Date(1994, 12, 1),
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'not-disclosed',
                    postalCode: '24000'
                },
                position: 'System Administrator',
                startOfEmployment: new Date(2020, 1, 25),
                gender: 'Male',
                adminPrivileges: true,
                hrManagementAccess: false,
            },
            {
                email: 'joe.average@company.io',
                password: await bcrypt.hash('notmypet', salt),
                firstName: 'Joe',
                middleName: '',
                lastName: 'Average',
                dateOfBirth: new Date(1994, 12, 1),
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'not-disclosed',
                    postalCode: '24000'
                },
                position: 'IT technician',
                startOfEmployment: new Date(2020, 1, 25),
                gender: 'Male',
                adminPrivileges: false,
                hrManagementAccess: false,
            },
        ]
        await User.deleteMany();
        await User.insertMany(users);
        console.log('Users seeded successfully!');
        mongodb.connection.close();        
    } catch (error) {
        console.error('Failed seeding users: ', error);
        mongodb.connection.close();        
    }
}

seedUsers();