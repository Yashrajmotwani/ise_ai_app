import requests # type: ignore
import certifi # type: ignore
import urllib3 # type: ignore
from bs4 import BeautifulSoup

# Disable SSL warnings
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)


links = {"iittp": {"cse": "https://cse.iittp.ac.in/faculty/",
                   "ee": "https://ee.iittp.ac.in/Faculty.html",
                   "chemical" : "https://iittp.ac.in/chemical-engineering-department",
                   "chemistry" : "https://iittp.ac.in/chemistry-department",
                   "civil" : "https://iittp.ac.in/civilengineering-department",
                   "hass" : "https://iittp.ac.in/humanities-and-social-sciences-department",
                   "maths" : "https://iittp.ac.in/mathematics-department",
                   "mechanical" : "https://iittp.ac.in/mechanical-engineering-department",
                   "physics" : "https://iittp.ac.in/physics-department",
                   },
            
         
         }

def scrape_website(college, department):
    
    url = "https://ee.iittp.ac.in/Faculty.html"  # Replace with the target URL
    # print(links[college])
    # for dept, link in links[college].items():
    #     print(dept, link)
    #     if dept == department:
    #         url = link
    #         break
    # print("url", url)
    
    response = requests.get(url, verify=False)
    print("response", response)
    # Check for request success
    if response.status_code != 200:
        return {"error": "Failed to fetch data", "status_code": response.status_code}

    soup = BeautifulSoup(response.text, "html.parser")
   
    items = []
   
    # Extract desired data (adjust selectors as per the site structure)
    for element in soup.find_all("div", class_="team-info"):
        print("inside ")
        name = element.find("h4").text.strip()
        position = element.find("p").text.strip()
        degree = element.find_all("p", class_="text-dark")[1].text.strip()
        areas_of_interest = element.find_all("p", class_="text-dark")[2].text.strip()
        phone_number = element.find_all("p", class_="text-dark")[3].text.strip()
        email = element.find_all("p", class_="text-dark")[4].text.strip()
        
        # Append the scraped data into a dictionary
        items.append({
            "name": name,
            "position": position,
            "degree": degree,
            "areas_of_interest": areas_of_interest,
            "phone_number": phone_number,
            "email": email
        })
    print(items)
    return items

# Example usage
if __name__ == "__main__":
    scraped_data = scrape_website()
    print(scraped_data)

    


# scrape_website()