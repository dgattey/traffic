#!/bin/sh
exec scala "$0" "$@"
!#
import java.io._
import java.net._

  object HelloWorld {
    def main(args: Array[String]) {
      if (args.length < 1) {
        println("Usage: ./badRequest.sh <host>")
        System.exit(1)
      }
      var s = new Socket(args(0), 9999)
      var output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream))
      output.write("BAD_REQUEST\n");
      output.flush()

      var input = new BufferedReader(new InputStreamReader(s.getInputStream))
      var line = input.readLine()
      s.shutdownOutput();
      while(line != null) {
        println(line)
        line = input.readLine()
      }

      s = new Socket(args(0), 9999)
      output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream))
      output.write("@q:AC:\n");
      output.write("@x\n");
      output.flush()

      input = new BufferedReader(new InputStreamReader(s.getInputStream))
      line = input.readLine()
      s.shutdownOutput();
      while(line != null) {
        println(line)
        line = input.readLine()
      }


      s = new Socket(args(0), 9999)
      output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream))
      output.write("@q:ac:");
      output.write("\n");
      output.write("Thay");
      output.write("\n");
      output.write("@x");
      output.write("\n");
      output.flush()

      input = new BufferedReader(new InputStreamReader(s.getInputStream))
      line = input.readLine()
      s.shutdownOutput();
      while(line != null) {
        println(line)
        line = input.readLine()
      }

      s = new Socket(args(0), 9999)
      output = new BufferedWriter(new OutputStreamWriter(s.getOutputStream))
      output.write("@q:rs:");
      output.write("\n");
      output.write("Thayer Street");
      output.write("\n");
      output.write("Cushing Street");
      output.write("\n");
      output.write("Thayer Street");
      output.write("\n");
      output.write("Waterman Street");
      output.write("\n");
      output.write("@x");
      output.write("\n");
      output.flush()

      input = new BufferedReader(new InputStreamReader(s.getInputStream))
      line = input.readLine()
      s.shutdownOutput();
      while(line != null) {
        println(line)
        line = input.readLine()
      }




	
    }
  }
  HelloWorld.main(args)
