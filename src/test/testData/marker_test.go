package books_test

import (
	"github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

var _ = ginkgo.Describe("Ginkgo", func() {
	ginkgo.Describe("Describe", func() {
		ginkgo.Context("MultipleContext", func() {
			ginkgo.It("it should be true", func() {
				Expect(true).To(BeTrue())
			})
			ginkgo.When("when true", func() {
				Expect(true).To(BeTrue())
			})
			ginkgo.Specify("specify true", func() {
				Expect(true).To(BeTrue())
			})
			ginkgo.DescribeTable("Table",
				func(actual string, expected string) {
					Expect(actual).To(Equal(expected))
				},
				ginkgo.Entry("true", "true", "true"),
				ginkgo.Entry("false", "false", "false"),
			)
		})
	})

	ginkgo.FDescribe("Describe", func() {
		ginkgo.FContext("MultipleContext", func() {
			ginkgo.FIt("it should be true", func() {
				Expect(true).To(BeTrue())
			})
			ginkgo.FWhen("when true", func() {
				Expect(true).To(BeTrue())
			})
			ginkgo.FSpecify("specify true", func() {
				Expect(true).To(BeTrue())
			})
			ginkgo.FDescribeTable("Table",
				func(actual string, expected string) {
					Expect(actual).To(Equal(expected))
				},
				ginkgo.FEntry("true", "true", "true"),
				ginkgo.FEntry("false", "false", "false"),
			)
		})
	})
})
