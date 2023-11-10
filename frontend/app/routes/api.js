const express = require('express');
const router = express.Router();
const apiController = require("../controllers/apiController");

router.get('/elections', apiController.getAllElections);
router.get('/elections/:electionId', apiController.getElection);

module.exports = router;
