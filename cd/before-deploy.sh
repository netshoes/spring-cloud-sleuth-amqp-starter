#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'master' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_4c61ff45a8d9_key -iv $encrypted_4c61ff45a8d9_iv -in cd/codesigning.asc.enc -out cd/codesigning.asc -d
    gpg --fast-import cd/codesigning.asc
fi