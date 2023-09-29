package main

import (
	"fmt"
	"log"
	"time"
)

// var whatToSay string : scope of whatToSay (1st letter is lower case) variable is package level : same as "private" access specifier in java,
// var WhatToSay string : scope of WhatToSay (1st letter is upper case) variable : can be accessed outside of package : same as "public" access specifier in java, same case with functions as well

var packageLevelVar string = "hi"  //local block scope variables always takes precedence over package level variables if they are with same name

//  custom object type : struct
type User struct {
	FirstName   string
	LastName    string
	PhoneNumber string
	Age         int
	BirthDate   time.Time
}

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

	// Pointers:
	var myString string = "Green"

	log.Println("myString is set to", myString)
	changeUsingPointer(&myString)
	log.Println("after func call myString is set to", myString)

	// accessing objects created from struct
	user := User {
		FirstName: "Trevor",
		LastName: "Sawler",
		PhoneNumber: "1 555 555 1212",
	}

	log.Println(user.FirstName, user.LastName, "Birthdate:", user.BirthDate)
}

// custom functions 
func saySomething() (string, string) {
	return "something", "else"
}

func changeUsingPointer(s *string) {
	log.Println("s is set to", s)
	newValue := "Red"
	*s = newValue
}