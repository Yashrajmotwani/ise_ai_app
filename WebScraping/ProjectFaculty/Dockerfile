# Use an official Python image as a base
FROM python:3.9-slim

# Install system dependencies for Chrome and Chromedriver
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb && \
    apt-get install -y ./google-chrome-stable_current_amd64.deb && \
    wget https://storage.googleapis.com/chrome-for-testing-public/132.0.6834.83/linux64/chromedriver-linux64.zip && \
    unzip chromedriver-linux64.zip -d /usr/bin/ && \
    rm chromedriver-linux64.zip

# Set environment variables for Chrome and Chromedriver
ENV CHROME_BIN=/usr/bin/google-chrome
ENV CHROME_DRIVER=/usr/bin/chromedriver

# Set working directory inside the container
WORKDIR /app

# Copy all files from your current directory (Flask app, requirements.txt) to /app directory in the container
COPY . /app

# Install Python dependencies
RUN pip install --upgrade pip
RUN pip install -r requirements.txt

# Expose port (default Flask port is 5000)
EXPOSE 5000

# Set the default command to run your Flask app
# For hosting the app using Flask
CMD ["python", "my_flask_app.py"]
# For hosting the app using Gunicorn
# CMD ["gunicorn", "-w", "4", "-b", "0.0.0.0:5000", "my_flask_app:app"]
