from pymongo import MongoClient # type: ignore
from datetime import datetime
import re

# Connect to MongoDB
client = MongoClient("mongodb+srv://iseiittpdev2025:8BU6VsHeEKsGpPQm@cluster0.x64u9.mongodb.net/IITDB?retryWrites=true&w=majority&appName=Cluster0")
db = client["IITDBLLM"]  # database name
# collection = db["IITFaculty"]  # collection name
collection = db["IITProjects"]  # collection name

# Mapping of old values to new values
current_date = datetime.today().strftime("%d.%m.%Y")
# current_date = datetime.today()

# Fetch documents where college is "IIT Hyderabad"
documents = collection.find({"college": "IIT Tirupati"})

for doc in documents:
    
    last_date_str = doc.get("last_date", "").strip()
    print("last date str : ", last_date_str)
    if not last_date_str or last_date_str.upper() == "NA" or last_date_str.upper() == "N/A":
        status = "NA"  # If last_date is "NA" or missing, set status as "NA"
    else:
        try:
            last_date = datetime.strptime(last_date_str, "%d.%m.%Y")
            status = "open" if last_date >= datetime.strptime(current_date, "%d.%m.%Y") else "closed"
            # last_date = datetime.strptime(last_date_str, "%d %B, %Y")
            # status = "open" if last_date >= current_date else "closed"
        except ValueError:
            print(f"Skipping invalid date format for document ID {doc['_id']}: {last_date_str}")
            continue

    # Update document in MongoDB
    print(f"Updating status for document ID {doc['_id']} with status: {status}")
    collection.update_one({"_id": doc["_id"]}, {"$set": {"status": status}})

print("Status update completed for IIT Hyderabad.")
# for doc in documents:
#     last_date_str = doc.get("last_date", "").strip()
#     print("last date str : ", last_date_str)
    
#     match = re.search(r"(\d{1,2} [A-Za-z]+, \d{4})", last_date_str)

#     if match:
#         cleaned_date = match.group(1)  # Extract the first valid date
#         print("cleaned", cleaned_date)  # Output: 20 December, 2023
#         collection.update_one({"_id": doc["_id"]}, {"$set": {"last_date": cleaned_date}})
#     else:
#         print("No valid date found")
    
