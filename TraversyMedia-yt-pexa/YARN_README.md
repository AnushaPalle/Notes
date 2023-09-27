To install yarn into mac:  
brew install yarn  
yarn -v  

yarn init  
yarn add package_name  
yarn add package_name@version  
yarn install -> to install dependencies mentioned in package.json  
yarn remove package_name -> remove package from package.json dependency  
yarn outdated -> gives outdated packages  
yarn upgrade  
yarn upgrade package_name -> to upgrade a package  
yarn global add package_name -> adds package global  
yarn global bin -> get location of globally installed packages  
yarn global remove package_name -> remove global package  
yarn list  
yarn add package_name --dev -> add package as dev dependency , instead of --dev : we can use '-D'  
yarn check -> checks versions of packages in yarn.lock and package.json  
yarn import -> generates yarn.lock  
yarn licenses list  
yarn pack -> zips packages into a zip file  
yarn cache list  
yarn cache clean -> clear the cache  