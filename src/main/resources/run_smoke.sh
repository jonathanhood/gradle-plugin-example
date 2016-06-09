#!/bin/bash

# Setup Virtual Environment
if [ ! -d "venv" ]; then
    virtualenv venv
fi

source venv/bin/activate
pip install --upgrade -r requirements.txt

# Run Tests
ffin -s ${HOSTNAME}
RESULT=$?

# Clean Up
deactivate
exit $RESULT