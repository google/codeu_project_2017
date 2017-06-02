#!/usr/bin/python

# Copyright 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the 'License');
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an 'AS IS' BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

###############################################################################
# BUILD/ALL.py
###############################################################################
# This build script is responsible for building and running all modules in
# this project. The following commands are supported:
#
#   clean : Remove all files in the output directory. This will not remove the
#           the output directory itself to allow the output directory to be
#           a symlink.
#
#   build : Build the full project. This will build all java files found in any
#           of the src directories.
#
#   rebuild : Call the clean and the build commands. For more information on
#             CLEAN and BUILD see the above entries.
#
#   run <class path> [ arguments ... ] : Run the specified class. All arguments
#                                        after the class path will be passed to
#                                        the java class when it runs.
###############################################################################


import os
import shutil
import subprocess
import sys


config = {
  'out' : 'bin',
  'src' : [ 'src', 'test' ],
  'libraries' : [
    'third_party/junit-4.12.jar',
    'third_party/hamcrest-core-1.3.jar',
    'third_party/database-api/commons-logging-1.1.1.jar',
    'third_party/database-api/firebase-admin-4.1.7.jar',
    'third_party/database-api/google-api-client-1.22.0.jar',
    'third_party/database-api/google-api-client-gson-1.22.0.jar',
    'third_party/database-api/google-api-client-protobuf-1.22.0.jar',
    'third_party/database-api/google-api-client-xml-1.22.0.jar',
    'third_party/database-api/google-http-client-1.22.0.jar',
    'third_party/database-api/google-http-client-jackson2-1.22.0.jar',
    'third_party/database-api/google-oauth-client-1.22.0.jar',
    'third_party/database-api/gson-2.1.jar',
    'third_party/database-api/guava-21.0.jar',
    'third_party/database-api/httpclient-4.0.1.jar',
    'third_party/database-api/httpcore-4.0.1.jar',
    'third_party/database-api/jackson-core-2.1.3.jar',
    'third_party/database-api/jackson-core-asl-1.9.11.jar',
    'third_party/database-api/jdo2-api-2.3-eb.jar',
    'third_party/database-api/json-20140107.jar',
    'third_party/database-api/jsr305-1.3.9.jar',
    'third_party/database-api/protobuf-java-2.6.1.jar',
    'third_party/database-api/xpp3-1.1.4c.jar'
  ],
  'separators' : {
    'nt' : ';',
    'posix' : ':'
  }
}


def clean(config) :
  out = config['out']

  for entry in [ os.path.join(out, name) for name in os.listdir(out) ] :
    if os.path.isdir(entry) :
      shutil.rmtree(entry)
    else :
      os.remove(entry)

  print('Clean PASSED')


def build(config) :
  libraries = config['libraries']
  out = config['out']
  separator = config['separators'][os.name]
  src = config['src']

  # Find all the source files in the given source directories. Only allow java files
  # because javac will fail if a non-java file is given.
  src_files = [ ]
  for src_path in src :
    for root, dirs, files in os.walk(src_path) :
      src_files += [ os.path.join(root, file) for file in files if file.endswith('.java') ]

  # Take all the data from above and build a single command that can
  # be ran to build the project.
  command = [ ]
  command += [ 'javac' ]
  command += [ '-d', out ]
  command += [ '-cp', separator.join([ out ] + libraries) ]
  command += [ '-Xlint' ]
  command += src_files

  print('running : %s' % command)
  print('Build %s' % ('PASSED' if subprocess.call(command) == 0 else 'FAILED'))


# Run a class from the project
def run(config, start_class_path, arguments):
  libraries = config['libraries']
  out = config['out']
  separator = config['separators'][os.name]

  command = [ ]
  command += [ 'java' ]
  command += [ '-cp', separator.join([ out ] + libraries) ]
  command += [ start_class_path ]
  command += arguments

  print('running : %s' % command)
  print('Build %s' % ('PASSED' if subprocess.call(command) == 0 else 'FAILED'))


def handle_command(config, args):
  if 'clean' == args[0] :
    clean(config)
  elif 'build' == args[0] :
    build(config)
  elif 'rebuild' == args[0] :
    clean(config)
    build(config)
  elif 'run' == args[0] :
    run(config, args[1], args[2:])
  else :
    print('Unknown command "%s"' % args)


# Remove the first argument as that will be the name of this file
handle_command(config, sys.argv[1:])
