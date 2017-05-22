#!/usr/bin/python

# Copyright 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This script gets log files from the CodeU "Nanny" hosting service.
# It accepts the following parameters:
#   HOST - The TCP-IP address of the hosting system.
#   PORT - The port of the log-fetching service.
#   cmd - a single-character command, either "w" for watch or "p" for pull.
#     w: watch: similar to "tail -f".
#     p: pull: read the entire log and store locally.
#   team - the team name, or "relay" to fetch the relay service log.
#   outfile - optional, only needed for "p" command; local filename to
#             hold the fetched log.
#

import socket
import sys

# Current host address and log fetch port for CodeU Nanny hosting service.
global HOST
global PORT

def usage() :
  print 'Usage: lf_client.py <host-addr> <server-port> <w(atch)|p(ull)> <teamname|relay> [outfile-for-pull]'


def watch(sock, cmd, team):
  try:
    sock.connect((HOST, PORT))
    sock.sendall(cmd + " " + team + "\n")

    while (True):
      buffer = sock.recv(1024)
      if (len(buffer) == 0):
        break
      print buffer,

  finally:
    sock.close()


def pull(sock, cmd, team, outfile):
  try:
    sock.connect((HOST, PORT))
    sock.sendall(cmd + " " + team + "\n")

    with open(outfile, 'w') as f:
      while (True):
        buffer = sock.recv(4096)
        if (len(buffer) == 0):
          break
        f.write(buffer)

  finally:
    sock.close()


def main(args) :
  if len(args) < 2 :
    print 'ERROR: too few arguments:'
    usage()
    exit(1)

  cmd = " ".join(args[0])
  team = args[1]

  if ('p' in cmd) :
    if len(args) < 3 :
      print 'ERROR: too few arguments:'
      usage()
      exit(1)
    outfile = args[2]

  sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

  if ('p' in cmd):
    pull(sock, cmd, team, outfile)
  elif ('w' in cmd):
    watch(sock, cmd, team)
  else :
    print 'ERROR: unrecognized command:'
    usage()
    exit(1)

if __name__ == "__main__" :
  if len(sys.argv) < 4 :
    print 'ERROR: no arguments supplied.'
    usage()
    exit(1)
  HOST = sys.argv[1]
  PORT = int(sys.argv[2])
  main(sys.argv[3:])
