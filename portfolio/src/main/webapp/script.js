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

getComments(5);

// Submits a comment to be stored in the server.
document.querySelector('.comment-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  const response = await fetch('/comment', {
    method: 'POST',
    body: new URLSearchParams(new FormData(e.target)),
  });
  const json = await response.json();
  if ('error' in json) {
    console.log('Could not submit comment');
    return;
  }
  appendComment(document.querySelector('.comment-box').value);
});

async function getComments(num) {
  const response = await fetch(`/comment?num=${num}`);
  const json = await response.json();
  const commentContainer = document.querySelector('.comments');
  commentContainer.innerHTML = '';
  for (const comment of json.comments) {
    appendComment(comment);
  }
}

function rerequestComments() {
  let numCommentsElem = document.querySelector('.num-comments');
  let num = numCommentsElem.options[numCommentsElem.selectedIndex].value;
  getComments(num);
}

function toggleComments() {
  let comments = document.querySelector('.comments');
  comments.style.display = (comments.style.display == 'none') ? 'block' : 'none';
}

function appendComment(value) {
  const commentContainer = document.querySelector('.comments');
  let liComment = document.createElement('li');
  liComment.innerText = value;
  liComment.className = 'comment';
  commentContainer.append(liComment);
}

async function deleteComments() {
  const response = await fetch('/delete', {
    method: 'POST',
  });
  const json = await response.json();
  if ('error' in json) {
    console.log('Could not delete comments');
    return;
  }
  const commentContainer = document.querySelector('.comments');
  commentContainer.innerHTML = '';
};
