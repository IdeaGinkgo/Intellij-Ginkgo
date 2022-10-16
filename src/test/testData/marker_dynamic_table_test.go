package books_test

import (
	. "github.com/onsi/ginkgo/v2"
	. "github.com/onsi/gomega"
)

var _ = Describe("Ginkgo", func() {
	Describe("Describe", func() {
		Context("MultipleContext", func() {
            DescribeTable("addition",
                func(a, b, c int) {
                    Expect(a + b).To(Equal(c))
                },
                EntryDescription("%d + %d = %d"),
                Entry(nil, 1, 2, 3),
                Entry(EntryDescription("%[3]d = %[1]d + %[2]d"), 10, 100, 110),
                Entry(func(a, b, c int) string { return fmt.Sprintf("%d = %d", a+b, c) }, 4, 3, 7),
            )
		})
	})

	FDescribe("Describe", func() {
		FContext("MultipleContext", func() {
            FDescribeTable("addition",
                func(a, b, c int) {
                    Expect(a + b).To(Equal(c))
                },
                EntryDescription("%d + %d = %d"),
                FEntry(nil, 1, 2, 3),
                FEntry(EntryDescription("%[3]d = %[1]d + %[2]d"), 10, 100, 110),
                FEntry(func(a, b, c int) string { return fmt.Sprintf("%d = %d", a+b, c) }, 4, 3, 7),
            )
		})
	})
})