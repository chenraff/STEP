// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the 'License');
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an 'AS IS' BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

const DISPLAY = {
    NONE: 'none',
    BLOCK: 'block'
}
const URLS = {
    RANDOM_PHARSE: '/random-phrase',
    DATA: '/data',
    DELETE_DATA: '/delete-data'
}
const RESPONSES = {
    GET: 'GET',
    POST: 'POST'
}
const DOM_ELEM = {
    ORDERED_LIST: 'ol',
    LIST_ITEM: 'li'
}
const DOM_IDS = {
    SLIDES: '.slide',
    PHRASE_CONTAINER: 'phrase-container',
    COMMENTS_CONTAINER: 'comments-container',
    COMMENTS_AMOUNT: 'comments-amount'
}
const MESSAGES = {
    NO_COMMENTS_MSG: 'There are no comments yet.',
    PHRASE_DISPLALY_ERROR: 'Phrase displaying process failed',
    COMMENTS_DISPLAY_ERROR: 'Comments displaying process failed',
    COMMENTS_DELETE_ERROR: 'Comments deletion process failed'
}
const MAX_COMMENTS_QUERY_STR = 'maxComments=';


let currSlideIndex = 0;
const slides = document.querySelectorAll(DOM_IDS.SLIDES);
showSlide(currSlideIndex);

// Modulus operator (for positive and negative numbers)
function mod(a,b) {
    return ((a % b) + b) % b; 
} 

// Displays the next/previous slide based on the advanceAmount 
function advanceSlide(advanceAmount) {
    showSlide(currSlideIndex += advanceAmount);
}

// Displays the slide according to given slideIndex
function showSlide(slideIndex) {
    slides.forEach((slide) => slide.style.display = DISPLAY.NONE);
    slideIndex = mod(slideIndex, slides.length);
    slides[slideIndex].style.display = DISPLAY.BLOCK;
}

// Sends GET request to the RandomPhraseServlet 
// and displays the returned phrase at the relevant div
function getChinesePhrase() {
    fetch(URLS.RANDOM_PHARSE).then((response) => response.text())
        .then((phrase) => {
            document.getElementById(DOM_IDS.PHRASE_CONTAINER).innerText = phrase;
        }).catch((err) => {
            console.log(err);
            window.alert(MESSAGES.PHRASE_DISPLALY_ERROR);
        });
}

// Sends GET request to the DataServlet 
// and displays the returned comments as list
function getComments() {
    const commentsMaxAmount = document.getElementById(DOM_IDS.COMMENTS_AMOUNT).value;
    const commentsContainerElem = document.getElementById(DOM_IDS.COMMENTS_CONTAINER);
    const commentsURL = URLS.DATA + '?' + MAX_COMMENTS_QUERY_STR + commentsMaxAmount;
    
    // Clear previous shown comments 
    commentsContainerElem.innerHTML = '';
    
    fetch(commentsURL).then((response) => response.json()).then((commArray) => {
        if (commArray.length > 0) {
            commentsContainerElem.appendChild(arrayToOL(commArray));    
        }
        else {
            commentsContainerElem.innerText = MESSAGES.NO_COMMENTS_MSG;
        }
    }).catch((err) => {
        console.log(err);
        window.alert(MESSAGES.COMMENTS_DISPLAY_ERROR);
    });
}

// Transforms given array to HTML ol element
function arrayToOL(array) {
    const list = document.createElement(DOM_ELEM.ORDERED_LIST);
    array.forEach((item) => {
        let listItem = document.createElement(DOM_ELEM.LIST_ITEM);
        listItem.appendChild(document.createTextNode(item));
        list.appendChild(listItem);
    })
    return list;
}

// Sends POST request to DeleteDataServlet that deletes the comments
function clearComments() {
    fetch(URLS.DELETE_DATA, {method: RESPONSES.POST})
        .then(getComments())
        .catch((err) => {
            console.log(err);
            window.alert(MESSAGES.COMMENTS_DELETE_ERROR);
        });
}