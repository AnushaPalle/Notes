package main

import "fmt"

var packageLevelVar string = "hi"

func main() {
	fmt.Println("Hello Ganesh !!")

	// variables
	var whatToSay string
	var i int

	whatToSay = "Goodbye, world"
	fmt.Println(whatToSay)

	i = 7

	fmt.Println("i is set to", i)

	whatWasSaid, theOtherThingThatWasSaid := saySomething() // := assignes return value type of saysomething function to whatWasSaid, theOtherThingThatWasSaid variable type 

	fmt.Println("The function returned", whatWasSaid, theOtherThingThatWasSaid, "packageLevel Variable:", packageLevelVar)
}

// custom functions 
func saySomething() (string, string) {
	return "something", "else"
}