🚀 IIT Connect - Data Extraction & Database Setup



⚙️ Step 1: Install Required Libraries
Run the following command in Google Colab to install dependencies:
```
!pip install requests beautifulsoup4
```

🌐 Step 2: Scrape IIT Data 

Run the script iit_scraper.py in Google Colab:
```
import requests
from bs4 import BeautifulSoup
import json
import re

python script to add iit college data 
..................................

```

📥 Step 3: Download JSON File in Colab

Run this in Google Colab to download the file:
```
from google.colab import files
files.download("iit_data.json")
```

🗄️ Step 4: Upload JSON to MongoDB (Atlas + Compass)

🔹 4.1 Create a MongoDB Atlas Cluster

1️⃣ Go to MongoDB Atlas and create a free cluster.

2️⃣ In Database Access, create a new user with a username and password.

3️⃣ In Network Access, allow your IP address.

4️⃣ Get the MongoDB Connection URI, example:
```
mongodb+srv://your_username:your_password@your_cluster.mongodb.net/
```

🔹 4.2 Import JSON into MongoDB Compass

1️⃣ Open MongoDB Compass and connect using the MongoDB Atlas URI.

2️⃣ Click "Create Database" → Name it "iit_connect".

3️⃣ Click "Create Collection" → Name it "iit_colleges".

4️⃣ Click "Import Data", select "iit_data.json", and click "Import".

📢 Step 5: Verify Data in MongoDB

Run this query in MongoDB Compass or MongoDB Shell to check if data is inserted correctly:
```
db.iit_colleges.find().pretty()
```

## 🔧 Troubleshooting  

| Issue | Solution |
|--------|---------|
| `requests.exceptions.ConnectionError` | Check internet connection & retry |
| `Permission denied for download` | Run `files.download()` again in Colab |
| MongoDB connection error | Ensure **correct Atlas URI** in Compass |
