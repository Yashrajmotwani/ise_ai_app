from pymongo import MongoClient # type: ignore

# Connect to MongoDB
client = MongoClient("mongodb+srv://iseiittpdev2025:8BU6VsHeEKsGpPQm@cluster0.x64u9.mongodb.net/IITDB?retryWrites=true&w=majority&appName=Cluster0")
db = client["IITDB"]  # database name
# collection = db["IITFaculty"]  # collection name
collection = db["IITProjects"]  # collection name

# Mapping of old values to new values
updates = {
    "iittp": "IIT Tirupati",
    "iitg": "IIT Gandhi Nagar",
    "iitk": "IIT Kanpur",
    "iith" : "IIT Hyderabad"
}

# looging through the mapping and update documents
for old_value, new_value in updates.items():
    result = collection.update_many(
        {"college": old_value},  # get documents with the old value
        {"$set": {"college": new_value}}  # setting new value
    )
    print(f"Updated {result.modified_count} documents where college was '{old_value}'.")

print("Update complete.")

# Update only documents where college is "IIT Kanpur"
# filter_query = {"college": "IIT Kanpur", "advertisement_link": {"$not": {"$regex": "^http"}}}
# update_query = {
#     "$set": {
#         "advertisement_link": {
#             "$concat": ["https://iitk.ac.in", "$advertisement_link"]
#         }
#     }
# }

# # Perform the update
# collection.update_many(filter_query, update_query)

# print("Advertisement links updated successfully for IIT Kanpur!")


# Find documents with incorrect advertisement_link structure
# documents = collection.find({"college": "IIT Kanpur"})

# # Update each document
# for doc in documents:
#     adv_link = doc.get("advertisement_link", {})

#     # If it's stored incorrectly as an object, extract the correct string
#     if isinstance(adv_link, dict) and "$concat" in adv_link:
#         parts = adv_link["$concat"]  # Extract the array elements
#         if isinstance(parts, list) and len(parts) == 2:
#             new_link = parts[0] + parts[1]  # Join them as a string
#             collection.update_one({"_id": doc["_id"]}, {"$set": {"advertisement_link": new_link}})

# print("Fixed advertisement_link field for IIT Kanpur!")