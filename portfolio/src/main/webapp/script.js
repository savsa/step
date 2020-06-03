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

getComments();

// Submits a comment to be stored in the server.
document.querySelector('.comment-form').addEventListener('submit', async function(e) {
  e.preventDefault();
  const response = await fetch('/comment', {
    method: 'POST',
    body: new URLSearchParams(new FormData(event.target)),
  });
  const json = await response.json();
  if ('error' in json) {
    console.log('Could not submit comment');
  }
  const commentContainer = document.querySelector('.comments');
  let liComment = document.createElement('li');
  liComment.innerText = document.querySelector('.comment-box').value;
  liComment.className = 'comment';
  commentContainer.append(liComment);
});

async function getComments() {
  const response = await fetch('/comment');
  const json = await response.json();
  const commentContainer = document.querySelector('.comments');
  let liComment;
  for (const comment of json.comments) {
    liComment = document.createElement('li');
    liComment.innerText = comment;
    liComment.className = 'comment';
    commentContainer.append(liComment);
  }
}

function toggleComments() {
  let comments = document.querySelector('.comments');
  comments.style.display = (comments.style.display == 'none') ? 'block' : 'none';
}
