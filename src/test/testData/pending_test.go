package books_test

import (
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

var _ = FDescribe("Ginkgo", func() {
	PDescribe("Describe", func() {
		PContext("MultipleContext", func() {
			PIt("it should be true", func() {
				Expect(true).To(BeTrue())
			})
			PWhen("when true", func() {
				Expect(true).To(BeTrue())
			})
			PSpecify("specify true", func() {
				Expect(true).To(BeTrue())
			})
			PDescribeTable("Table",
				func(actual string, expected string) {
					Expect(actual).To(Equal(expected))
				},
				PEntry("true", "true", "true"),
				PEntry("false", "false", "false"),
			)
		})
	})

	XDescribe("Describe", func() {
		XContext("MultipleContext", func() {
			XIt("it should be true", func() {
				Expect(true).To(BeTrue())
			})
			XWhen("when true", func() {
				Expect(true).To(BeTrue())
			})
			XSpecify("specify true", func() {
				Expect(true).To(BeTrue())
			})
			XDescribeTable("Table",
				func(actual string, expected string) {
					Expect(actual).To(Equal(expected))
				},
				XEntry("true", "true", "true"),
				XEntry("false", "false", "false"),
			)
		})
	})
})