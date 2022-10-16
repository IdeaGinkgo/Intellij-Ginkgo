package books_test

import (
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

var _ = Describe("Ginkgo", func() {
	Describe("Describe", func() {
		Context("MultipleContext", func() {
			It("it should be true", func() {
				Expect(true).To(BeTrue())
			})
			When("when true", func() {
				Expect(true).To(BeTrue())
			})
			Specify("specify true", func() {
				Expect(true).To(BeTrue())
			})
			DescribeTable("Table",
				func(actual string, expected string) {
					Expect(actual).To(Equal(expected))
				},
				Entry("true", "true", "true"),
				Entry("false", "false", "false"),
			)
		})
	})

	FDescribe("Describe", func() {
		FContext("MultipleContext", func() {
			FIt("it should be true", func() {
				Expect(true).To(BeTrue())
			})
			FWhen("when true", func() {
				Expect(true).To(BeTrue())
			})
			FSpecify("specify true", func() {
				Expect(true).To(BeTrue())
			})
			FDescribeTable("Table",
				func(actual string, expected string) {
					Expect(actual).To(Equal(expected))
				},
				FEntry("true", "true", "true"),
				FEntry("false", "false", "false"),
			)
		})
	})

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
