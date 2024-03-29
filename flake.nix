{
  inputs = {
    nixpkgs.url = "github:nixos/nixpkgs/nixos-23.11";
  };

  outputs = { self, nixpkgs }:
    let
      system = "x86_64-linux";
      pkgs = import nixpkgs { inherit system; };
    in
    {
      devShells.${system}.default = (pkgs.buildFHSEnv {
        name = "java-fhs";
        targetPkgs = pkgs: with pkgs; [
          # deps
          libGL

          # IDE
          jetbrains.jdk
          jetbrains.idea-community
        ];
        runScript = ''
          #/usr/bin/env bash

          #echo "Starting IntelliJ IDEA Community Edition..."
          #bash -c "idea-community 2>1 > /dev/null"

          zsh
        '';
      }).env;
    };
}
