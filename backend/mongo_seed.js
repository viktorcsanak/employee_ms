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
                dateOfBirth: new Date(2000, 5, 1, 22, 0, 0), //Months are zero indexed //Months are zero indexed
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'undisclosed address',
                    postalCode: '24000'
                },
                position: 'Medior Embedded Software Engineer',
                startOfEmployment: new Date(2022, 6, 1, 22, 0, 0), //Months are zero indexed
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
                dateOfBirth: new Date(1989, 2, 12, 22, 0, 0), //Months are zero indexed
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'undisclosed address',
                    postalCode: '24000'
                },
                position: 'Lead HR Manager',
                startOfEmployment: new Date(2015, 7, 30, 22, 0, 0), //Months are zero indexed
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
                dateOfBirth: new Date(1994, 11, 1, 22, 0, 0), //Months are zero indexed
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'undisclosed address',
                    postalCode: '24000'
                },
                position: 'System Administrator',
                startOfEmployment: new Date(2020, 0, 25, 22, 0, 0), //Months are zero indexed
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
                dateOfBirth: new Date(1994, 11, 1, 22, 0, 0), //Months are zero indexed
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'undisclosed address',
                    postalCode: '24000'
                },
                position: 'IT technician',
                startOfEmployment: new Date(2020, 0, 25, 22, 0, 0), //Months are zero indexed
                gender: 'Male',
                adminPrivileges: false,
                hrManagementAccess: false,
            },
        ]

        const firstNames = ['John', 'Alice', 'Michael', 'Sara', 'David', 'Emma', 'Chris', 'Lara', 'Tom', 'Sophia'];
        const lastNames = ['Smith', 'Johnson', 'Williams', 'Jones', 'Brown', 'Davis', 'Miller', 'Wilson', 'Taylor', 'Moore'];
        const positions = ['Software Engineer', 'HR Specialist', 'Data Analyst', 'Product Manager', 'Designer', 'Project Coordinator'];

        for (let i = 0; i < 96; i++) { // 96 more users to reach 100 total
            const randomFirstName = firstNames[Math.floor(Math.random() * firstNames.length)];
            const randomLastName = lastNames[Math.floor(Math.random() * lastNames.length)];
            const randomEmail = `${randomFirstName.toLowerCase()}.${randomLastName.toLowerCase()}${i + 5}@company.io`; // Unique email
            const randomPosition = positions[Math.floor(Math.random() * positions.length)];
            const isAdmin = i % 20 === 0; // Every 10th user is an admin
            const hasHRAccess = i % 15 === 0; // Every 5th user has HR access

            users.push({
                email: randomEmail,
                password: await bcrypt.hash('password123', salt),
                firstName: randomFirstName,
                middleName: '',
                lastName: randomLastName,
                dateOfBirth: new Date(1990 + (i % 30), Math.floor(Math.random() * 12), Math.floor(Math.random() * 28) + 1, 22, 0, 0), // Random date of birth
                placeOfResidence: {
                    city: 'Szabadka',
                    address: 'undisclosed address',
                    postalCode: '24000'
                },
                position: randomPosition,
                startOfEmployment: new Date(2020, Math.floor(Math.random() * 12), Math.floor(Math.random() * 28) + 1, 22, 0, 0), // Random employment start date
                gender: 'Other',
                adminPrivileges: isAdmin,
                hrManagementAccess: hasHRAccess,
            });
        }

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
