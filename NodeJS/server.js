const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();
const port = 5000;

// Middleware
app.use(cors());
app.use(express.json());

// MongoDB Connection
const uri = "mongodb+srv://iseiittpdev2025:8BU6VsHeEKsGpPQm@cluster0.x64u9.mongodb.net/IITDBLLM?retryWrites=true&w=majority&appName=Cluster0";
mongoose.connect(uri, { useNewUrlParser: true, useUnifiedTopology: true })
  .then(() => console.log('Connected to MongoDB Atlas'))
  .catch((err) => console.error('Error connecting to MongoDB Atlas:', err));

// Schemas & Models
const projectSchema = new mongoose.Schema({
  name_of_post: String,
  discipline: String,
  posting_date: String,
  last_date: String,
  pi_name: String,
  advertisement_link: String,
  status: String,
  college: String,
  department: String
}, { collection: 'IITProjects' });

const teacherSchema = new mongoose.Schema({
  name: String,
  position: String,
  qualification: String,
  areas_of_interest: String,
  phone: String,
  email: String,
  image_link: String,
  college: String,
  department: String
}, { collection: 'IITFaculty' });

const userProjectSchema = new mongoose.Schema({
  userId: String,
  favoriteProjects: [{ type: mongoose.Schema.Types.ObjectId, ref: 'IITProjects' }]
});

const userTeacherSchema = new mongoose.Schema({
  userId: String,
  favoriteTeachers: [{ type: mongoose.Schema.Types.ObjectId, ref: 'IITFaculty' }]
});

const collegeSchema = new mongoose.Schema({
  No: String,
  Name: String,
  Abbreviation: String,
  Founded: String,
  'Converted as IIT': String,
  'State/UT': String,
  website: String,
  Faculty: String,
  Students: String,
  Logo: String
}, { collection: 'IITColleges' });

const ProjectModel = mongoose.model('IITProjects', projectSchema);
const TeacherModel = mongoose.model('IITFaculty', teacherSchema);
const UserProjectModel = mongoose.model('UserProjects', userProjectSchema);
const UserTeacherModel = mongoose.model('UserTeachers', userTeacherSchema);
const CollegeModel = mongoose.model('IITColleges', collegeSchema);

// API Routes
app.get('/search', async (req, res) => {
  try {
    const query = req.query.query;
    const results = await ProjectModel.find({
      $or: [
        { name_of_post: new RegExp(query, 'i') },
        { discipline: new RegExp(query, 'i') },
        { last_date: new RegExp(query, 'i') },
        { pi_name: new RegExp(query, 'i') },
        { status: new RegExp(query, 'i') },
        { college: new RegExp(query, 'i') },
        { department: new RegExp(query, 'i') }
      ]
    });
    res.json(results.length ? results : { message: 'No matching records found.' });
  } catch (err) {
    res.status(500).json({ message: 'Error fetching data from MongoDB', error: err });
  }
});

app.get('/fsearch', async (req, res) => {
  try {
    const query = req.query.query;
    const results = await TeacherModel.find({
      $or: [
        { areas_of_interest: new RegExp(query, 'i') },
        { name: new RegExp(query, 'i') },
        { position: new RegExp(query, 'i') },
        { qualification: new RegExp(query, 'i') },
        { college: new RegExp(query, 'i') },
        { department: new RegExp(query, 'i') }
      ]
    });
    res.json(results.length ? results : { message: 'No matching records found.' });
  } catch (err) {
    res.status(500).json({ message: 'Error fetching data from MongoDB', error: err });
  }
});

app.get('/colleges', async (req, res) => {
  try {
    const colleges = await CollegeModel.find();
    res.json(colleges);
  } catch (err) {
    res.status(500).json({ message: 'Error fetching colleges', error: err });
  }
});

// Save and Remove Project
app.post('/saveProject/:userId', async (req, res) => {
  const { userId } = req.params;
  const projectData = req.body;
  try {
    const project = await ProjectModel.findById(projectData._id);
    if (!project) return res.status(404).json({ message: 'Project not found' });
    let user = await UserProjectModel.findOne({ userId });
    if (!user) {
      user = new UserProjectModel({ userId, favoriteProjects: [projectData._id] });
    } else if (!user.favoriteProjects.includes(projectData._id)) {
      user.favoriteProjects.push(projectData._id);
    }
    await user.save();
    res.status(200).json({ message: 'Project saved successfully' });
  } catch (err) {
    res.status(500).json({ message: 'Error saving project', error: err });
  }
});

app.delete('/removeProject/:userId/:projectId', async (req, res) => {
  const { userId, projectId } = req.params;
  try {
    const user = await UserProjectModel.findOne({ userId });
    if (!user) return res.status(404).json({ message: 'User not found' });
    user.favoriteProjects = user.favoriteProjects.filter(id => id.toString() !== projectId);
    await user.save();
    res.status(200).json({ message: 'Project removed from favorites' });
  } catch (err) {
    res.status(500).json({ message: 'Error removing project', error: err });
  }
});

// Save and Remove Teacher
app.post('/saveTeacher/:userId', async (req, res) => {
  const { userId } = req.params;
  const teacherData = req.body;
  try {
    const teacher = await TeacherModel.findById(teacherData._id);
    if (!teacher) return res.status(404).json({ message: 'Teacher not found' });
    let user = await UserTeacherModel.findOne({ userId });
    if (!user) {
      user = new UserTeacherModel({ userId, favoriteTeachers: [teacherData._id] });
    } else if (!user.favoriteTeachers.includes(teacherData._id)) {
      user.favoriteTeachers.push(teacherData._id);
    }
    await user.save();
    res.status(200).json({ message: 'Teacher saved successfully' });
  } catch (err) {
    res.status(500).json({ message: 'Error saving teacher', error: err });
  }
});

app.delete('/removeTeacher/:userId/:teacherId', async (req, res) => {
  const { userId, teacherId } = req.params;
  try {
    const user = await UserTeacherModel.findOne({ userId });
    if (!user) return res.status(404).json({ message: 'User not found' });
    user.favoriteTeachers = user.favoriteTeachers.filter(id => id.toString() !== teacherId);
    await user.save();
    res.status(200).json({ message: 'Teacher removed from favorites' });
  } catch (err) {
    res.status(500).json({ message: 'Error removing teacher', error: err });
  }
});

// Get user's favorite projects
app.get('/getFavoriteProjects/:userId', async (req, res) => {
  const { userId } = req.params;

  try {
    // Find the user and populate the favorite projects with full details
    const user = await UserProjectModel.findOne({ userId }).populate('favoriteProjects');
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    // Send the populated favorite projects
    res.status(200).json(user.favoriteProjects);
  } catch (err) {
    res.status(500).json({ message: 'Error fetching favorite projects', error: err });
  }
});

// Get user's favorite projects
app.get('/getFavoriteTeacher/:userId', async (req, res) => {
  const { userId } = req.params;

  try {
    // Find the user and populate the favorite projects with full details
    const user = await UserTeacherModel.findOne({ userId }).populate('favoriteTeachers');
    if (!user) {
      return res.status(404).json({ message: 'User not found' });
    }

    // Send the populated favorite projects
    res.status(200).json(user.favoriteTeachers);
  } catch (err) {
    res.status(500).json({ message: 'Error fetching favorite teachers', error: err });
  }
});

// Start Server
app.listen(port, '0.0.0.0', () => {
  console.log(`Server running on http://localhost:${port}`);
});

