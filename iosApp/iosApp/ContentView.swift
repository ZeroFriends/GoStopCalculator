import SwiftUI

struct ContentView: View {
    var body: some View {
        VStack(spacing: 16) {
            Image(systemName: "calculator")
                .imageScale(.large)
                .foregroundColor(.accentColor)
            Text("GoStopCalculator KMP iOS")
                .font(.title)
                .fontWeight(.bold)
            Text("Domain & Data KMP Core Connected!")
                .font(.subheadline)
                .foregroundColor(.gray)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}
