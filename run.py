import subprocess
import argparse

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("filename")
    args = parser.parse_args()

    if args.filename == None:
        print("Usage: python3 run.py [filename]");
    
    name = args.filename;
    subprocess.run(['java', '-cp', 'bin:antlr-4.13.2-complete.jar', 'simplf.Simplf', name])