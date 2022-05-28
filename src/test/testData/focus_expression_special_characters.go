package test

var _ = Describe("Book", func() {
	Describe("Categorizing book length", func() {
		When("When Test", func() {
			<caret>It("should be a (short) story", func() {
				Expect(shortBook.CategoryByLength()).To(Equal("SHORT STORY"))
			})
		})
	})
})
