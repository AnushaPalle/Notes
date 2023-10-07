package main

import (
	"fmt"
	"log"
	"sort"
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

func (u *User) getFirstName() string {
	return u.FirstName
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
	//  struct with receiver function: accessing first name through reciver function attached to struct
	log.Println(user.getFirstName())

	// Map
	mymap := make(map[string]string)
	mymap["dog"] = "harry"
	fmt.Println(mymap["dog"])

	// map with custom type
	myMap := make(map[string]User)

	me := User{
		FirstName: "Trevor",
		LastName:  "Sawler",
	}

	myMap["me"] = me

	log.Println(myMap["me"].FirstName)

	// Slice
	var mySlice []int 
	mySlice = append(mySlice, 3)
	mySlice = append(mySlice, 1)
	mySlice = append(mySlice, 2)
	fmt.Println(mySlice)
	sort.Ints(mySlice)
	fmt.Println(mySlice)

	// shorthand for slices

	numbers := []int{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}
	log.Println(numbers)

	// print first two elements of slice, starting at first position
	log.Println(numbers[0:2])

	// create a slice of strings
	names := []string{"one", "seven", "fish", "cat"}
	log.Println(names)

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