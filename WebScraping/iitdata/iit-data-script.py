# Install required libraries in Colab (only needed once)
!pip install requests beautifulsoup4

import requests
from bs4 import BeautifulSoup
import json
import re  # Import regex to clean data

# Wikipedia IIT page URL
url = "https://en.wikipedia.org/wiki/Indian_Institutes_of_Technology"

# Send a request to fetch page content
response = requests.get(url)
soup = BeautifulSoup(response.text, "html.parser")

# Locate the correct table
tables = soup.find_all("table", {"class": "wikitable"})
iit_table = tables[0]  # The first table on the page

# Extract data from table rows
iit_data = []
headers = [th.text.strip() for th in iit_table.find_all("th")]

for row in iit_table.find_all("tr")[1:]:  # Skip the header row
    cells = row.find_all("td")
    if len(cells) > 0:
        # Remove reference numbers like [15]
        iit_info = {headers[i]: re.sub(r"\[\d+\]", "", cells[i].text.strip()) for i in range(len(cells))}

        # Find IIT logo (first <img> tag in the row)
        logo_img = row.find("img")
        if logo_img:
            logo_url = "https:" + logo_img["src"]  # Convert to full URL
        else:
            logo_url = ""  # No logo found

        iit_info["Logo"] = logo_url  # Add logo URL to JSON

        iit_data.append(iit_info)

# Save data to JSON file
file_name = "iit_data.json"
with open(file_name, "w", encoding="utf-8") as json_file:
    json.dump(iit_data, json_file, indent=4, ensure_ascii=False)

print("✅ Scraping complete! Data saved in 'iit_data.json'.")

# Download the JSON file in Colab
from google.colab import files
files.download(file_name)
