# Import necessary libraries
import requests  # To send HTTP requests and fetch webpage content
from bs4 import BeautifulSoup  # To parse HTML content
import json  # To save extracted data in JSON format
import re  # To clean text data using regex

# Define the URL of the Wikipedia page containing IIT details
url = "https://en.wikipedia.org/wiki/Indian_Institutes_of_Technology"

# Send a GET request to fetch the webpage content
response = requests.get(url)

# Parse the webpage content using BeautifulSoup
soup = BeautifulSoup(response.text, "html.parser")

# Locate all tables with the class 'wikitable' (tables containing IIT information)
tables = soup.find_all("table", {"class": "wikitable"})

# Select the first table, as it contains the list of IITs
iit_table = tables[0]

# Extract column headers from the table
headers = [th.text.strip() for th in iit_table.find_all("th")]

# Initialize an empty list to store IIT data
iit_data = []

# Iterate through each row in the table (excluding the header row)
for row in iit_table.find_all("tr")[1:]:  
    cells = row.find_all("td")  # Extract all cells in the row

    # Only process rows that contain data
    if len(cells) > 0:
        # Extract and clean data from each cell
        iit_info = {headers[i]: re.sub(r"\[\d+\]", "", cells[i].text.strip()) for i in range(len(cells))}

        # Find the logo image (if available) in the current row
        logo_img = row.find("img")
        
        # If an image is found, construct the full logo URL
        if logo_img:
            logo_url = "https:" + logo_img["src"]  # Ensure it's a valid URL
        else:
            logo_url = ""  # No logo available
        
        # Add the logo URL to the IIT data dictionary
        iit_info["Logo"] = logo_url  

        # Append the extracted IIT information to the list
        iit_data.append(iit_info)

# Define the JSON filename for saving the extracted data
file_name = "iit_data.json"

# Save the IIT data in JSON format
with open(file_name, "w", encoding="utf-8") as json_file:
    json.dump(iit_data, json_file, indent=4, ensure_ascii=False)

# Print confirmation message after successful data extraction
print("✅ Scraping complete! Data saved in 'iit_data.json'.")
