# spring-boot-jpa-docker-jenkins-pipeline
<!-- PROJECT SHIELDS -->

<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->
[![Contributors][contributors-shield]][contributors-url]
[![Forks][forks-shield]][forks-url]
[![Stargazers][stars-shield]][stars-url]
[![Issues][issues-shield]][issues-url]
[![LinkedIn][linkedin-shield]][linkedin-url]

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[contributors-shield]: https://img.shields.io/github/contributors/yewin-mm/job-interview.svg?style=for-the-badge
[contributors-url]: https://github.com/yewin-mm/job-interview/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/yewin-mm/job-interview.svg?style=for-the-badge
[forks-url]: https://github.com/yewin-mm/job-interview/network/members
[stars-shield]: https://img.shields.io/github/stars/yewin-mm/job-interview.svg?style=for-the-badge
[stars-url]: https://github.com/yewin-mm/job-interview/stargazers
[issues-shield]: https://img.shields.io/github/issues/yewin-mm/job-interview.svg?style=for-the-badge
[issues-url]: https://github.com/yewin-mm/job-interview/issues
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/ye-win-1a33a292/




# job-interview-project
* This is sample Spring Boot application with JPA.

<!-- TABLE OF CONTENTS -->
## Table of Contents
- [About The Project](#about-the-project)
    - [Built With](#built-with)
- [Getting Started](#getting-started)
    - [Before you begin](#before-you-begin)
    - [Clone Project](#clone-project)
    - [Prerequisites](#prerequisites)
    - [Instruction](#instruction)
        -  [Testing](#testing)
- [Contact Me](#contact)
- [Contributing](#Contributing)


<a name="about-the-project"></a>
## ⚡️About The Project
This is the simple spring boot java project. <br>
Logic is just loading from input file and calculate as per currency and retrieving data. 


<a name="built-with"></a>
### 🪓 Built With
This project is built with
* [Java](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html)
* [Maven](https://maven.apache.org/download.cgi)
* [MySQL Database](https://github.com/yewin-mm/mysql-docker-container)


<a name="getting-started"></a>
## 🔥 Getting Started
This project purpose is to show my interviewer.
See the [Prerequisites](#prerequisites) sections for basic knowledge and go as per below [Instruction](#instruction) section.


<a name="before-you-begin"></a>
### 🔔 Before you begin
If you are new in Git, GitHub and new in Spring Boot configuration structure, <br>
You should see basic detail instructions first in here [Spring Boot Application Instruction](https://github.com/yewin-mm/spring-boot-app-instruction)<br>
If you are not good enough in basic API knowledge with Java Spring Boot and other spring basic knowledge, you should see below example projects first. <br>
Click below links.
* [Spring Boot Sample CRUD Application](https://github.com/yewin-mm/spring-boot-sample-crud) (for sample CRUD application)
* [Reading Values from Properties files](https://github.com/yewin-mm/reading-properties-file-values) (for reading values from properties files)
* [MySQL DB](https://github.com/yewin-mm/mysql-docker-container)

<a name="clone-project"></a>
### 🥡 Clone Project
* Clone the repo
   ```sh
   git clone https://github.com/yewin-mm/job-interview.git
   ```
  
<a name="prerequisites"></a>
### 🔑 Prerequisites
Prerequisites can be found here, [Spring Boot Application Instruction](https://github.com/yewin-mm/spring-boot-app-instruction). <br>

<a name="instruction"></a>
### 📝 Instruction
* Make sure MySQL DB version upper 8 is running in your local.
* Go to MySQL Console and crate database with name `job_interview`.
* Please make sure there is no port are running with 8080 in your machine, if not so, please change the port.
* You can see my Dynamic Reading from `application.properties` file and Salary Calculator based on your input data set.
* So, if you want to add new currency, just add your currency and value in `application.properties`.
* I just load data one time when the application was started.
* Please note that there were a lot of difference not only changing currencies, but also changing positions <br> 
and sometimes, two currencies are there and some are unstructured format. Even some have character encoding issue.
* I control all the things as much as I can and convert it to BAHT currency which is use as default currency in this application.


<a name="testing"></a>
#### Testing
* Import `job-interview.postman_collection.json` file under project directory (see that file in project directory) into your installed Postman application.
    * Click in your Postman (top left corner) import -> file -> upload files -> {choose that json file} and open/import.
    * After that, you can see the folder which you import from file in your Postman.
* There are total of 13 APIs in there, and you can test all APIs. 

***After that you can see the code***


<a name="contact"></a>
## ✉️ Contact Me
Name - Ye Win <br> LinkedIn profile -  [Ye Win](https://www.linkedin.com/in/ye-win-1a33a292/)  <br> Email Address - <a href="mailto:yewin.mmr@gmail.com?">yewin.mmr@gmail.com</a> <br> WhatsApp - [+959252656065](https://wa.me/959252656065?text=Hi) <br> Website - [My Website](https://yewin.me/)

Project Link: [Job Interview Sample Application](https://github.com/yewin-mm/job-interview)



<a name="contributing"></a>
## ⭐ Contributing
Contributions are what make the open source community such an amazing place to be learnt, inspire, and create. Any contributions you make are **greatly appreciated**.
<br>If you want to contribute....
1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/yourname`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeatures'`)
4. Push to the Branch (`git push -u origin feature/yourname`)
5. Open a Pull Request
