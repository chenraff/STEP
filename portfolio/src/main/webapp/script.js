// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const orderedList = 'ol';
const listItem = 'li';
const noCommentsMsg = "There are no comments yet.";

let currSlideIndex = 0;
const slides = document.querySelectorAll(".slide");
showSlide(currSlideIndex);

// Modulus operator (for positive and negative numbers)
function mod(a,b) {
    return ((a % b) + b) % b; 
} 

// Display the next/previous slide based on the advanceAmount 
function advanceSlide(advanceAmount){
    showSlide(currSlideIndex += advanceAmount);
}

// Displays the slide according to given slideIndex
function showSlide(slideIndex){
    slides.forEach(slide => slide.style.display = "none");
    slideIndex = mod(slideIndex, slides.length);
    slides[slideIndex].style.display = "block";
}

function getChinesePhrase() {
    fetch('/random-phrase').then((response) => response.text())
        .then((phrase) => 
                {document.getElementById('phrase-container').innerText = phrase;});
}

function saveComment() {
    fetch('/data', {method: 'POST'});
}

function getComments() {
    const commentsMaxAmount = document.getElementById("comments-amount").value;
    const commentsContainerElem = document.getElementById('comments-container');
    const commentsURL = '/data?maxComments=' + commentsMaxAmount;
    
    /* Clear previous shown comments */
    commentsContainerElem.innerHTML = "";
    
    fetch(commentsURL).then((response) => response.json()).then((commArray) => {
        if (commArray.length > 0){
            commentsContainerElem.appendChild(arrayToOL(commArray));    
        }
        else {
            commentsContainerElem.innerText = noCommentsMsg;
        }
    })
}

function arrayToOL(array) {
    const list = document.createElement(orderedList);
    for (let i = 0; i < array.length; i++) {
        let item = document.createElement(listItem);
        item.appendChild(document.createTextNode(array[i]));
        list.appendChild(item);
    }
    return list;
}

function clearComments() {
    //const request = new Request('/delete-data');
    //request.method = 'POST';
    fetch('/delete-data', {method: 'POST'}).then(getComments());
}