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

load();

// Submits a comment to be stored in the server.
document.querySelector('.comment-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  const response = await fetch('/comment', {
    method: 'POST',
    body: new URLSearchParams(new FormData(e.target)),
  });
  const json = await response.json();
  if (json.status == 'error') {
    console.log('Could not submit comment');
    return;
  }
  rerequestComments();
});

// Load the dynamic parts of the page like comments and the authentication status.
async function load() {
  const response = await fetch(`/comment?num=5`);
  const json = await response.json();
  appendComments(json.comments);

  let nav = document.querySelector('.nav-content');
  if (json.email == '') {
    let loginLink = document.createElement('a');
    loginLink.href = json.login_url;
    loginLink.className = 'login-link';
    loginLink.innerText = 'Log in';
    nav.append(loginLink);

    document.querySelector('.comment-form').style.display = 'none';
    return;
  }

  // The user is logged in.
  let p = document.createElement('p');
  p.innerText = 'Welcome, ' + json.email;
  nav.append(p);
}

async function getComments(num) {
  const response = await fetch(`/comment?num=${num}`);
  const json = await response.json();
  appendComments(json.comments);
}

function rerequestComments() {
  let numCommentsElem = document.querySelector('.num-comments');
  let num = numCommentsElem.options[numCommentsElem.selectedIndex].value;
  getComments(num);
}

function appendComments(comments) {
  const commentContainer = document.querySelector('.comments');
  commentContainer.innerHTML = '';
  for (const comment of comments) {
    appendComment(comment);
  }
}

function toggleComments() {
  let comments = document.querySelector('.comments');
  comments.style.display = (comments.style.display == 'none') ? 'block' : 'none';
}

function appendComment(comment) {
  const commentContainer = document.querySelector('.comments');
  let liComment = document.createElement('li');
  liComment.innerText = `${comment.email}: ${comment.text}`;
  liComment.className = 'comment';
  commentContainer.append(liComment);
}

async function deleteComments() {
  const response = await fetch('/delete', {
    method: 'POST',
  });
  const json = await response.json();
  if (json.status == 'error') {
    console.log('Could not delete comments');
    return;
  }
  const commentContainer = document.querySelector('.comments');
  commentContainer.innerHTML = '';
};

/* Creates a map showing Columbia University and adds it to the page. */
async function createMap() {
  const response = await fetch('/piano-data');
  const json = await response.json();
  const map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 40.808037, lng: -73.961982},
    zoom: 16,
  });

  for (piano of json) {
    createMarker(map, piano);
  }
}

function createMarker(map, piano) {
  const marker = new google.maps.Marker({
    position: {lat: piano.lat, lng: piano.lng},
    map: map,
    title: piano.title,
  });
  const infoWindow = new google.maps.InfoWindow({
    content: piano.info,
  });
  infoWindow.open(map, marker);
}