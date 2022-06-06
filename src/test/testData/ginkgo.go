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
})